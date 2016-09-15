package oing.webapp.android.sdkliteserver.tools.autoadd.executor;

import oing.webapp.android.sdkliteserver.tools.autoadd.command.Command;

import java.util.List;

public interface CommandListAware {
	void setCommandList(List<Command> listCommand);
}
