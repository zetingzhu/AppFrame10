package com.trade.zt_speechrecognizer.audio

/**
 * @author: zeting
 * @date: 2026/2/10
 * @description: WAV 文件写入器
 * 
 * 功能：
 * 1. 按照标准 WAV 格式 (RIFF WAVE) 写入音频数据
 * 2. 支持 PCM 16bit 格式
 * 3. 自动管理文件头信息的写入和更新
 * 
 * 参考资料：http://soundfile.sapp.org/doc/WaveFormat/
 */

import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WavFileWriter {

    private var fileOutputStream: FileOutputStream? = null
    private var dataSize = 0L // 记录纯音频数据的大小 (字节)

    /**
     * 打开 WAV 文件准备写入
     * 
     * @param filePath 文件绝对路径
     * @param sampleRate 采样率 (如 44100, 16000)
     * @param numChannels 声道数 (1: 单声道, 2: 立体声)
     * @param bitsPerSample 采样位数 (通常为 16)
     */
    @Throws(IOException::class)
    fun open(filePath: String, sampleRate: Int, numChannels: Int, bitsPerSample: Int) {
        close() // 确保先关闭之前的流

        fileOutputStream = FileOutputStream(filePath)
        dataSize = 0

        // 预写入 WAV 文件头 (占位 44 字节)
        writeWavHeader(sampleRate, numChannels, bitsPerSample)
    }

    /**
     * 写入音频数据 (ShortArray)
     * 适用于 16-bit PCM 数据
     * 
     * @param audioData PCM 音频数据数组
     */
    @Throws(IOException::class)
    fun write(audioData: ShortArray) {
        val outputStream = fileOutputStream ?: return

        // 将 ShortArray 转换为 ByteArray (WAV 是小端序)
        val byteBuffer = ByteBuffer.allocate(audioData.size * 2)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)

        for (sample in audioData) {
            byteBuffer.putShort(sample)
        }

        outputStream.write(byteBuffer.array())
        dataSize += audioData.size * 2L
    }

    /**
     * 写入音频数据 (ByteArray)
     * 
     * @param audioData 字节数组数据
     */
    @Throws(IOException::class)
    fun write(audioData: ByteArray) {
        fileOutputStream?.write(audioData)
        dataSize += audioData.size.toLong()
    }

    /**
     * 关闭文件
     * 重要：关闭前会回写文件头，更新文件总大小和数据块大小
     */
    fun close() {
        try {
            fileOutputStream?.let { outputStream ->
                // 更新文件头中的 data size 和 file size
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
     * 写入 WAV 文件头 (44字节)
     * 此时 ChunkSize 和 Subchunk2Size 暂时填 0，待关闭文件时回填
     */
    @Throws(IOException::class)
    private fun writeWavHeader(sampleRate: Int, numChannels: Int, bitsPerSample: Int) {
        val byteRate = sampleRate * numChannels * bitsPerSample / 8
        val blockAlign = numChannels * bitsPerSample / 8

        val header = ByteBuffer.allocate(44)
        header.order(ByteOrder.LITTLE_ENDIAN)

        // --- RIFF Chunk ---
        header.put("RIFF".toByteArray()) // ChunkID (4 bytes)
        header.putInt(0)                 // ChunkSize (4 bytes) - 稍后更新: 36 + dataSize
        header.put("WAVE".toByteArray()) // Format (4 bytes)

        // --- fmt Subchunk ---
        header.put("fmt ".toByteArray()) // Subchunk1ID (4 bytes)
        header.putInt(16)                // Subchunk1Size (4 bytes) - PCM 为 16
        header.putShort(1.toShort())     // AudioFormat (2 bytes) - 1 表示 PCM
        header.putShort(numChannels.toShort()) // NumChannels (2 bytes)
        header.putInt(sampleRate)        // SampleRate (4 bytes)
        header.putInt(byteRate)          // ByteRate (4 bytes)
        header.putShort(blockAlign.toShort())  // BlockAlign (2 bytes)
        header.putShort(bitsPerSample.toShort()) // BitsPerSample (2 bytes)

        // --- data Subchunk ---
        header.put("data".toByteArray()) // Subchunk2ID (4 bytes)
        header.putInt(0)                 // Subchunk2Size (4 bytes) - 稍后更新: dataSize

        fileOutputStream?.write(header.array())
    }

    /**
     * 更新 WAV 文件头中的大小信息
     * 需要使用 FileChannel 移动文件指针
     */
    @Throws(IOException::class)
    private fun updateWavHeader(outputStream: FileOutputStream) {
        // ChunkSize = 36 + dataSize (36 是头部其他字段的总长度: 44 - 8)
        // 44 - 8 是因为 ChunkID(4) + ChunkSize(4) 不包含在 ChunkSize 计算中
        val fileSize = 36 + dataSize 

        // 移动到 ChunkSize 位置 (offset 4)
        outputStream.channel.position(4)
        val sizeBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(fileSize.toInt())
        outputStream.write(sizeBuffer.array())

        // 移动到 Subchunk2Size 位置 (offset 40)
        outputStream.channel.position(40)
        val dataSizeBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dataSize.toInt())
        outputStream.write(dataSizeBuffer.array())
    }

    /**
     * 获取当前已写入的音频数据大小 (字节)
     */
    fun getDataSize(): Long = dataSize
}
