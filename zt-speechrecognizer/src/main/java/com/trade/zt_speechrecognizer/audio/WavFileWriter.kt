package com.trade.zt_speechrecognizer.audio

/**
 * @author: zeting
 * @date: 2026/2/10
 *
 */

import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * WAV文件写入器
 * 参考：http://soundfile.sapp.org/doc/WaveFormat/
 */
class WavFileWriter {

    private var fileOutputStream: FileOutputStream? = null
    private var dataSize = 0L

    /**
     * 打开WAV文件
     * @param filePath 文件路径
     * @param sampleRate 采样率
     * @param numChannels 声道数
     * @param bitsPerSample 采样位数
     */
    @Throws(IOException::class)
    fun open(filePath: String, sampleRate: Int, numChannels: Int, bitsPerSample: Int) {
        close() // 关闭已打开的文件

        fileOutputStream = FileOutputStream(filePath)
        dataSize = 0

        // 写入WAV文件头
        writeWavHeader(sampleRate, numChannels, bitsPerSample)
    }

    /**
     * 写入音频数据
     * @param audioData PCM音频数据
     */
    @Throws(IOException::class)
    fun write(audioData: ShortArray) {
        val outputStream = fileOutputStream ?: return

        // 将short数组转换为byte数组
        val byteBuffer = ByteBuffer.allocate(audioData.size * 2)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

        for (sample in audioData) {
            byteBuffer.putShort(sample)
        }

        outputStream.write(byteBuffer.array())
        dataSize += audioData.size * 2L
    }

    /**
     * 写入音频数据（byte数组）
     */
    @Throws(IOException::class)
    fun write(audioData: ByteArray) {
        fileOutputStream?.write(audioData)
        dataSize += audioData.size.toLong()
    }

    /**
     * 关闭文件并更新文件头
     */
    fun close() {
        try {
            fileOutputStream?.let { outputStream ->
                // 更新文件头中的data size和file size
                updateWavHeader(outputStream)
                outputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fileOutputStream = null
        }
    }

    /**
     * 写入WAV文件头
     */
    @Throws(IOException::class)
    private fun writeWavHeader(sampleRate: Int, numChannels: Int, bitsPerSample: Int) {
        val byteRate = sampleRate * numChannels * bitsPerSample / 8
        val blockAlign = numChannels * bitsPerSample / 8

        val header = ByteBuffer.allocate(44)
        header.order(ByteOrder.LITTLE_ENDIAN)

        // RIFF chunk
        header.put("RIFF".toByteArray()) // ChunkID
        header.putInt(0) // ChunkSize (将在关闭时更新)
        header.put("WAVE".toByteArray()) // Format

        // fmt sub-chunk
        header.put("fmt ".toByteArray()) // Subchunk1ID
        header.putInt(16) // Subchunk1Size (16 for PCM)
        header.putShort(1.toShort()) // AudioFormat (1 for PCM)
        header.putShort(numChannels.toShort()) // NumChannels
        header.putInt(sampleRate) // SampleRate
        header.putInt(byteRate) // ByteRate
        header.putShort(blockAlign.toShort()) // BlockAlign
        header.putShort(bitsPerSample.toShort()) // BitsPerSample

        // data sub-chunk
        header.put("data".toByteArray()) // Subchunk2ID
        header.putInt(0) // Subchunk2Size (将在关闭时更新)

        fileOutputStream?.write(header.array())
    }

    /**
     * 更新WAV文件头
     */
    @Throws(IOException::class)
    private fun updateWavHeader(outputStream: FileOutputStream) {
        val fileSize = 36 + dataSize // 36 = 44 - 8 (RIFF chunk size field is 4 bytes)

        outputStream.channel.position(4)
        val sizeBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(fileSize.toInt())
        outputStream.write(sizeBuffer.array())

        outputStream.channel.position(40)
        val dataSizeBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dataSize.toInt())
        outputStream.write(dataSizeBuffer.array())
    }

    /**
     * 获取已写入的数据大小（字节）
     */
    fun getDataSize(): Long = dataSize
}