package org.iips.actions.service;

import org.iips.actions.model.Task;
import org.iips.actions.repository.TaskRepository;
import org.iips.actions.exception.TaskNotFoundException;
import org.iips.actions.exception.InvalidTaskException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Service layer for task management. */
public class TaskService {
  private final TaskRepository repository;

  /**
   * Creates a TaskService with the given repository.
   *
   * @param repository TaskRepository implementation
   */
  public TaskService(TaskRepository repository) {
    this.repository = repository;
  }

  /**
   * Creates and saves a new task.
   *
   * @param description Task description
   * @param dueDate Due date (optional)
   * @return Saved task
   * @throws InvalidTaskException if description is invalid
   */
  public Task createTask(String description, LocalDate dueDate) {
    if (description == null || description.isBlank()) {
      throw new InvalidTaskException("Description cannot be null or blank");
    }
    Task task = new Task(UUID.randomUUID(), description, false, dueDate);
    return repository.save(task);
  }

  /**
   * Marks a task as completed.
   *
   * @param id Task id
   * @return Updated task
   * @throws TaskNotFoundException if task does not exist
   */
  public Task completeTask(UUID id) {
    return repository
        .findById(id)
        .map(t -> repository.save(new Task(t.id(), t.description(), true, t.dueDate())))
        .orElseThrow(() -> new TaskNotFoundException(id));
  }

  /**
   * Returns all tasks.
   *
   * @return List of tasks
   */
  public List<Task> getAllTasks() {
    return repository.findAll();
  }

  /**
   * Deletes a task by id.
   *
   * @param id Task id
   * @throws TaskNotFoundException if task does not exist
   */
  public void deleteTask(UUID id) {
    if (!repository.deleteById(id)) {
      throw new TaskNotFoundException(id);
    }
  }

  /**
   * Finds a task by id.
   *
   * @param id Task id
   * @return Task if found
   * @throws TaskNotFoundException if not found
   */
  public Task getTaskById(UUID id) {
    return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
  }
}
.