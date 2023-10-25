package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.path.AbsolutePath;
import seedu.address.model.statemanager.State;
import seedu.address.model.statemanager.TaskOperation;
import seedu.address.model.taskmanager.Task;

/**
 * The MarkCommand class represents a command for marking a task in the task list.
 * This command is typically used in a context where the user is viewing a list of tasks.
 * It marks a specific task in the list as completed.
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    public static final String MESSAGE_INCORRECT_STATE = "The current state is not showing task list.";

    public static final String MESSAGE_INVALID_INDEX = "The task index provided is invalid.";

    public static final String MESSAGE_MARK_TASK_SUCCESS = "Marked task: %1$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " [task index]";

    private static final Logger logger = LogsCenter.getLogger(MarkCommand.class);

    private final Index index;

    /**
     * Constructs a MarkCommand with the specified task index to be marked.
     *
     * @param index The index of the task to be marked.
     */
    public MarkCommand(Index index) {
        requireNonNull(index);
        this.index = index;
    }

    /**
     * Executes the MarkCommand to mark a task as completed.
     *
     * @param state The current state of the application.
     * @return A CommandResult containing a message indicating the success of the marking operation.
     * @throws CommandException If the command cannot be executed due to an incorrect state (not showing task list).
     */
    @Override
    public CommandResult execute(State state) throws CommandException {
        requireNonNull(state);

        logger.info("Executing mark task command...");

        if (!state.isShowTaskList()) {
            logger.warning("Task list is not shown. Aborting mark task command.");
            throw new CommandException(MESSAGE_INCORRECT_STATE);
        }

        AbsolutePath displayPath = state.getDisplayPath();
        TaskOperation taskOperation = state.taskOperation(displayPath);

        // Check if index is valid.
        if (!taskOperation.isValidIndex(this.index.getOneBased())) {
            logger.warning("Invalid index: " + this.index.getOneBased() + ". Aborting mark task command.");
            throw new CommandException(MESSAGE_INVALID_INDEX);
        }

        logger.info("Executing mark task command on index " + this.index.getOneBased());

        Task markedTask = taskOperation.markTask(this.index.getOneBased());
        state.updateList();

        logger.info("Task marked successfully. Marked task: " + markedTask.toString());

        return new CommandResult(String.format(MESSAGE_MARK_TASK_SUCCESS, markedTask));
    }

    /**
     * Checks if this MarkCommand is equal to another object. Two MarkCommand objects are considered equal
     * if they have the same task index to be marked.
     *
     * @param other The object to compare with this MarkCommand.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MarkCommand)) {
            return false;
        }

        MarkCommand otherMarkCommand = (MarkCommand) other;
        return this.index.equals(otherMarkCommand.index);
    }

    /**
     * Returns a string representation of this MarkCommand, including its index.
     *
     * @return A string representation of the MarkCommand.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", this.index)
                .toString();
    }
}