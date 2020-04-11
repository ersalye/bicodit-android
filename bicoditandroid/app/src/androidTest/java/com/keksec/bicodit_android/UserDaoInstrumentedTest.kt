package com.keksec.bicodit_android

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.db.AppDatabase
import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import com.keksec.bicodit_android.core.utils.helpers.UserTestHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UserDao instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UserDaoInstrumentedTest {

    private var db: AppDatabase? = null
    private var userDao: UserDao? = null

    /**
     * Initialising database and user dao
     */
    @Before
    @Throws(Exception::class)
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        )
            .build()
        userDao = db!!.userDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        db!!.close()
    }

    @Test
    fun whenInsertUserThenGetByIdTheSameOne() {
        val user: UserData = UserTestHelper.createUser()
        userDao!!.insert(user)
        val dbUser: UserData = userDao!!.getUserById(user.id)
        assertThat(dbUser).isEqualTo(user)
    }

    @Test
    fun whenInsertUserWithDuplicateIdThenReplaceOne() {
        val user1: UserData = UserTestHelper.createUser()
        val user2: UserData = UserTestHelper.createUser()
        user2.id = user1.id
        userDao!!.insert(user1)
        userDao!!.insert(user2)
        val dbUser: UserData = userDao!!.getUserById(user1.id)
        assertThat(dbUser).isEqualTo(user2)
    }

    @Test
    fun whenInsertUserThenDeleteSuccessfully() {
        val user: UserData = UserTestHelper.createUser()
        userDao!!.insert(user)
        userDao!!.delete(user)
        assertThat(userDao!!.getUserById(user.id)).isEqualTo(null)
    }

    @Test
    fun whenInsertUserThenDeleteByIdSuccessfully() {
        val user: UserData = UserTestHelper.createUser()
        userDao!!.insert(user)
        userDao!!.deleteById(user.id)
        assertThat(userDao!!.getUserById(user.id)).isEqualTo(null)
    }

    @Test
    fun whenInsertMultipleUsersThenGetAllSuccessfully() {
        val user1: UserData = UserTestHelper.createUser()
        val user2: UserData = UserTestHelper.createUser()
        val user3: UserData = UserTestHelper.createUser()
        userDao!!.insertAll(listOf(user1, user2, user3))
        val users = userDao!!.getAll()
        assertThat(users[0]).isEqualTo(user1)
        assertThat(users[1]).isEqualTo(user2)
        assertThat(users[2]).isEqualTo(user3)
    }

    @Test
    fun whenInsertMultipleUsersThenDeleteAllSuccessfully() {
        val user1: UserData = UserTestHelper.createUser()
        val user2: UserData = UserTestHelper.createUser()
        val user3: UserData = UserTestHelper.createUser()
        userDao!!.insertAll(listOf(user1, user2, user3))
        userDao!!.deleteAll()
        val users = userDao!!.getAll()
        assertThat(users).isEmpty()
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun whenUpdateUserThenReadTheSameOne() {
        val user: UserData = UserTestHelper.createUser()
        userDao!!.insert(user)
        user.email = "mr_keksec@keksec.com"
        userDao!!.update(user)
        val dbUser: UserData = userDao!!.getUserById(user.id)
        assertThat(dbUser).isEqualTo(user)
    }
}

