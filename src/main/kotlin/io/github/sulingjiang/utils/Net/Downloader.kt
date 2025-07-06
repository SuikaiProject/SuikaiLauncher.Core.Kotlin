package io.github.sulingjiang.utils.Net

import com.sun.nio.sctp.InvalidStreamException
import io.github.shimoranla.utils.basic.FileIO
import io.github.shimoranla.utils.basic.net.HttpRequestOptions
import io.github.shimoranla.utils.basic.net.HttpWebRequest
import io.github.sulingjiang.utils.net.NetFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.abs

enum class DownloadState{
    Awaiting,
    Processing
}

class Downloader {
    companion object {
        private val downloadListLock = Mutex()
        private val downloadState: DownloadState = DownloadState.Awaiting
        private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        private val downloadWorkers: MutableList<Job> = mutableListOf()
        private val taskChannel = Channel<NetFile>(Channel.UNLIMITED)

        suspend fun DownloadAsync(file: NetFile) {
            file.splitFile().forEach { netfile -> taskChannel.send(netfile) }
        }

        @Synchronized
        fun setWorkerCount(workerCount: Int) {
            if (workerCount > 384) throw IllegalArgumentException("指定的协程数过大")
            var diff = workerCount - downloadWorkers.size
            if (diff < 0) {
                diff += -diff * 2
                while (diff > 0) {
                    taskChannel.trySend(
                        NetFile(
                            "",
                            -2L,
                            null,
                            ""
                        )
                    )
                    diff--
                }
            } else {
                while (diff > 0) {
                    downloadWorkers.add(scope.launch {
                        for (task in taskChannel) {
                            if (task.fileSize == -2L) return@launch
                            HttpWebRequest.getServerResponse(
                                HttpRequestOptions(
                                    task.currentUrl,
                                    "GET",
                                    if (task.starts != -1L && task.ends != -1L) mutableMapOf<String, String>(
                                        "range" to "b=${task.starts}-${task.ends}"
                                    ) else null
                                )
                            ).body?.byteStream().use { netStream ->
                                if (netStream == null) task.onFiled(InvalidStreamException("无效的网络流"))
                                FileIO.getFileOutputStream(task.filePath.toString()).use { stream ->
                                    netStream!!.copyTo(stream)
                                }
                            }
                        }
                    }
                    )
                }
            }
        }
    }
}