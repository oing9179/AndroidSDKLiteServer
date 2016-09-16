package oing.webapp.android.sdkliteserver.tools.autoadd.command;

public interface Command<T> {
	String getDescription();

	T execute() throws Exception;
}
