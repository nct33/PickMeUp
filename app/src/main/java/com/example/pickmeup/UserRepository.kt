package com.example.pickmeup

class UserRepository private constructor(private var session: Session?) {

    fun getList() : Session? {
        return instance.session
    }

    companion object {
        private var instance = UserRepository(null)

        fun getInstance() : Session? {
            return instance.getList()
        }

        fun setUser(session : Session) {
            instance = UserRepository(session)
        }

        fun getSession() : Session? {
            return instance.session
        }

        fun empty() {
            instance = UserRepository(null)
        }
    }
}