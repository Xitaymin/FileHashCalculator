package exceptions

class TaskAlreadyExistsException(message: String) : RuntimeException(message)

class FilePathNotFoundException(message: String) : RuntimeException(message)

class TaskNotFoundException(message: String) : RuntimeException(message)

