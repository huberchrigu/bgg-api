package ch.chrigu.bgg.user.api

import ch.chrigu.bgg.core.api.BggServiceException

class UserNotFoundException(userId: String) : BggServiceException("There is no user with ID $userId")
