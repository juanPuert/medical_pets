package com.aistudio.petcare.data.repository

import android.content.Context
import com.aistudio.petcare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

data class ForumPost(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = 0,
    val petType: String? = null
)

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val country: String = "",
    val city: String = ""
)

class FirestoreRepository(context: Context) {
    private val dbId = context.getString(R.string.firestore_database_id)
    private val db = FirebaseFirestore.getInstance(dbId)
    private val auth = FirebaseAuth.getInstance()

    suspend fun getUserProfile(): UserProfile? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc = db.collection("users").document(uid).get().await()
            doc.toObject(UserProfile::class.java)?.copy(uid = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).set(profile).await()
    }

    fun getForumPosts(): Flow<List<ForumPost>> {
        return db.collection("forum_posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    ForumPost(
                        id = doc.id,
                        userId = data["userId"] as? String ?: "",
                        userName = data["userName"] as? String ?: "",
                        title = data["title"] as? String ?: "",
                        content = data["content"] as? String ?: "",
                        timestamp = (data["timestamp"] as? com.google.firebase.Timestamp)?.toDate()?.time ?: 0,
                        petType = data["petType"] as? String
                    )
                }
            }
    }

    suspend fun createPost(title: String, content: String, petType: String? = null) {
        val user = auth.currentUser ?: return
        val post = mapOf(
            "userId" to user.uid,
            "userName" to (user.displayName ?: "Anonymous"),
            "title" to title,
            "content" to content,
            "timestamp" to FieldValue.serverTimestamp(),
            "petType" to petType
        )
        db.collection("forum_posts").add(post).await()
    }
}
