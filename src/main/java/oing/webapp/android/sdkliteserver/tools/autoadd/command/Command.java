package oing.webapp.android.sdkliteserver.tools.autoadd.command;

public interface Command<T> {
	T execute() throws Exception;
}
