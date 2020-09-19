package com.devlomi.fireapp.common.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.view.ContextMenu
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import java.util.regex.Matcher
import java.util.regex.Pattern

fun Location.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)
fun LatLng.toLatLngString(): String = "${this.latitude},${this.longitude}"

fun DatabaseReference.toDeffered(): Deferred<DataSnapshot> {
    val deferred = CompletableDeferred<DataSnapshot>()

    deferred.invokeOnCompletion {
        if (deferred.isCancelled) {
            // optional, handle coroutine cancellation however you'd like here
        }
    }

    this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            deferred.completeExceptionally(p0.toException())
        }

        override fun onDataChange(p0: DataSnapshot) {
            deferred.complete(p0)
        }
    })
    return deferred
}

fun Query.toDeffered(): Deferred<DataSnapshot> {
    val deferred = CompletableDeferred<DataSnapshot>()

    deferred.invokeOnCompletion {
        if (deferred.isCancelled) {
            // optional, handle coroutine cancellation however you'd like here
        }
    }

    this.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            deferred.completeExceptionally(p0.toException())
        }

        override fun onDataChange(p0: DataSnapshot) {
            deferred.complete(p0)
        }
    })
    return deferred
}

fun Task<Void>.toDeffered(): Deferred<Boolean> {

    val deferred = CompletableDeferred<Boolean>()

    deferred.invokeOnCompletion {
        if (deferred.isCancelled) {
            // optional, handle coroutine cancellation however you'd like here
        }
    }

    this.addOnCompleteListener {
        deferred.complete(it.isSuccessful)
    }



    return deferred
}

fun StorageTask<FileDownloadTask.TaskSnapshot>.toDeffered(): Deferred<Boolean> {

    val deferred = CompletableDeferred<Boolean>()

    deferred.invokeOnCompletion {
        if (deferred.isCancelled) {
            // optional, handle coroutine cancellation however you'd like here
        }
    }

    this.addOnCompleteListener {
        deferred.complete(it.isSuccessful)
    }



    return deferred
}

fun StorageTask<UploadTask.TaskSnapshot>.toDefferedWithTask(): CompletableDeferred<Task<UploadTask.TaskSnapshot>> {

    val deferred = CompletableDeferred<Task<UploadTask.TaskSnapshot>>()

    deferred.invokeOnCompletion {
        if (deferred.isCancelled) {
            // optional, handle coroutine cancellation however you'd like here
        }
    }

    this.addOnCompleteListener {
        deferred.complete(it)
    }



    return deferred
}

fun Task<Uri>.toDefferedWithTask(): CompletableDeferred<Uri> {

    val deferred = CompletableDeferred<Uri>()

    deferred.invokeOnCompletion {
        if (deferred.isCancelled) {
            // optional, handle coroutine cancellation however you'd like here
        }
    }

    this.addOnCompleteListener {
        if (it.isSuccessful)
            deferred.complete(it.result!!)
        else
            deferred.completeExceptionally(Exception())
    }



    return deferred
}

fun FragmentManager.findFragmentByTagForViewPager(@IdRes viewPagerId: Int, currentItem: Int): Fragment? {
    return this.findFragmentByTag("android:switcher:$viewPagerId:$currentItem");

}

fun View.setHidden(boolean: Boolean, invisible: Boolean = false) {
    visibility = if (boolean) {
        if (invisible)
            View.INVISIBLE
        else
            View.GONE
    } else {
        View.VISIBLE
    }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide(invisible: Boolean) {
    visibility = if (invisible)
        View.INVISIBLE
    else
        View.GONE
}

fun Drawable.tint(context: Context, @ColorRes color: Int) {
    DrawableCompat.setTintMode(this, android.graphics.PorterDuff.Mode.SRC_IN)
    DrawableCompat.setTint(this, ContextCompat.getColor(context, color))
}

fun String.isColor(): Boolean {
    val colorPattern: Pattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})")
    val m: Matcher = colorPattern.matcher(this)
    return m.matches()
}
