package com.sevencrayons.compass

import android.bluetooth.BluetoothSocket
import android.os.Handler
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 *
 * @Description:     开启线程 无线读取数据
 * @Author:         liufei
 * @CreateDate:     2019/12/21 8:49
 */
class ConnectedThread constructor(var mmSocket: BluetoothSocket, var mHandler: Handler) : Thread() {

    companion object{
        val MESSAGE_READ:Int= 1
    }

    private val mmInStream: InputStream = mmSocket.inputStream
    private val mmOutStream: OutputStream = mmSocket.outputStream

    override fun run() {
        var bytes: Int
        ///开启无限读取
        while (true) {
            val buffer = ByteArray(100)
            try {
                // Read from the InputStream、
                bytes = mmInStream.read(buffer)
                // Send the obtained bytes to the UI activity
                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget()
            } catch (e: IOException) {
                break
            }

        }
    }

    fun write(bytes: ByteArray) {
        try {
            mmOutStream.write(bytes)
        } catch (e: IOException) {
        }

    }

    /* Call this from the main activity to shutdown the connection */
    fun cancel() {
        try {
            mmSocket.close()
        } catch (e: IOException) {
        }

    }
}

object BluetoothUtils {

    var bluetoothSocket: BluetoothSocket? = null
        get() = if (field != null) {
            field
        } else null

}