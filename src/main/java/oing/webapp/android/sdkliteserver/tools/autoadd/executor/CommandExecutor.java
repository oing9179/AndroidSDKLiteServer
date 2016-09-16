package oing.webapp.android.sdkliteserver.tools.autoadd.executor;

import oing.webapp.android.sdkliteserver.tools.autoadd.command.Command;
import org.apache.commons.lang3.Validate;

import java.util.List;

public class CommandExecutor {
	private List<Command> mListCommands = null;
	private CommandExecutionListener mListener = null;

	public CommandExecutor(List<Command> commands, CommandExecutionListener listener) {
		Validate.notNull(commands);
		Validate.notNull(listener);
		this.mListCommands = commands;
		this.mListener = listener;
	}

	public void execute() throws Exception {
		mListener.onPrepare();
		for (int i = 0; i < mListCommands.size(); i++) {
			Command lCommand = mListCommands.get(i);
			mListener.onPreExecute(mListCommands.size(), i, lCommand);
			if (lCommand instanceof CommandListAware) {
				((CommandListAware) lCommand).setCommandList(mListCommands);
			}
			lCommand.execute();
			mListener.onPostExecute(mListCommands.size(), i, lCommand);
		}
		mListener.onFinalize();
	}
}
