package server

enum class HttpStatus(val Code: Int) {

    Ok(200),
    BadRequest(400),
    NotFound(404),
    InternalServerError(500),

}