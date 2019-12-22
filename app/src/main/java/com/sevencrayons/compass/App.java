package com.sevencrayons.compass;

import android.app.Application;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

/**
 * @Description: 类作用描述
 * @Author: liufei
 * @CreateDate: 2019/11/30 14:36
 */
public class App extends Application {

    @Override
    public void onCreate() {
        Printer androidPrinter = new AndroidPrinter();
        Printer filePrinter = new FilePrinter                      // 打印日志到文件的打印器
                .Builder(getExternalCacheDir().getAbsoluteFile().getAbsolutePath()+"/xlog/")                              // 指定保存日志文件的路径
                .fileNameGenerator(new LogNameGenerator())
                .build();
        XLog.init(LogLevel.ALL,                                    // 指定日志级别，低于该级别的日志将不会被打印
                androidPrinter,
                filePrinter);
        XLog.i("日志初始化完成");
        super.onCreate();

    }
}
