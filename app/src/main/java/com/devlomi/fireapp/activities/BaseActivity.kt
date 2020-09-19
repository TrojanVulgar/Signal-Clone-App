package com.devlomi.fireapp.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.devlomi.fireapp.Base
import com.devlomi.fireapp.events.NavigateToLockActivity
import com.devlomi.fireapp.extensions.observeChildEvent
import com.devlomi.fireapp.extensions.setValueRx
import com.devlomi.fireapp.extensions.toMap
import com.devlomi.fireapp.model.constants.DBConstants
import com.devlomi.fireapp.model.constants.DownloadUploadStat.LOADING
import com.devlomi.fireapp.utils.*
import com.devlomi.fireapp.utils.biometricks.BiometricException
import com.devlomi.fireapp.utils.biometricks.BiometricPromptInfo
import com.devlomi.fireapp.utils.biometricks.Biometricks
import com.devlomi.fireapp.utils.biometricks.Biometricks.Available.Face
import com.devlomi.fireapp.utils.biometricks.Crypto
import com.devlomi.fireapp.utils.network.FireManager
import com.devlomi.fireapp.utils.update.UpdateChecker
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock


abstract class BaseActivity : AppCompatActivity(), Base {
    override val disposables = CompositeDisposable()
    abstract fun enablePresence(): Boolean
    private var presenceUtil: PresenceUtil? = null
    val fireManager = FireManager()
    private lateinit var newMessageHandler: NewMessageHandler

    //used to clean up like dismissing dialogs
    open fun goingToUpdateActivity() {}

    private var needsUpdate = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        needsUpdate = UpdateChecker(this).needsUpdate()
        if (!needsUpdate) {

            if (enablePresence())
                presenceUtil = PresenceUtil()

            newMessageHandler = NewMessageHandler(this, fireManager, disposables)
            //if user is coming from an old version, then delete the already received messages from his db
            if (SharedPreferencesManager.isDeletedUnfetchedMessage()) {
                attachNewMessageListener()
                attachDeletedMessageListener()
                attachNewGroupListener()
            }
        }
    }


    override fun onStart() {
        super.onStart()
        if (needsUpdate) {
            startUpdateActivity()
        }


    }


    fun startUpdateActivity() {
        goingToUpdateActivity()
        startActivity(Intent(this, UpdateActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }

    private fun attachNewGroupListener() {
        FireConstants.newGroups.child(FireManager.uid).observeChildEvent().subscribe { snap ->
            val dataSnapshot = snap.value

            if (dataSnapshot.value != null) {
                (dataSnapshot.child(DBConstants.GROUP_ID).value as? String)?.let { groupId ->
                    newMessageHandler.handleNewGroup(dataSnapshot.toMap())

                    deleteNewGroupEvent(groupId).subscribe().addTo(disposables)

                }
            }


        }.addTo(disposables)
    }

    private fun attachDeletedMessageListener() {
        FireConstants.deletedMessages.child(FireManager.uid).observeChildEvent().subscribe { snap ->
            val dataSnapshot = snap.value

            if (dataSnapshot.value != null) {
                (dataSnapshot.child(DBConstants.MESSAGE_ID).value as? String)?.let { messageId ->
                    newMessageHandler.handleDeletedMessage(dataSnapshot.toMap())

                    deleteDeletedMessage(messageId).subscribe().addTo(disposables)

                }
            }


        }.addTo(disposables)
    }


    private fun attachNewMessageListener() {
        FireConstants.userMessages.child(FireManager.uid).observeChildEvent().subscribe { snap ->
            val dataSnapshot = snap.value
            if (dataSnapshot.value != null) {
                (dataSnapshot.child(DBConstants.MESSAGE_ID).value as? String)?.let { messageId ->
                    val phone = dataSnapshot.child(DBConstants.PHONE).value as? String ?: ""
                    val message = MessageMapper.mapToMessage(dataSnapshot)

                    newMessageHandler.handleNewMessage(phone, message)

                    deleteMessage(messageId).subscribe().addTo(disposables)
                }

            }
        }.addTo(disposables)
    }

    private fun deleteMessage(messageId: String): Completable {
        return FireConstants.userMessages.child(FireManager.uid).child(messageId).setValueRx(null)
    }

    private fun deleteDeletedMessage(messageId: String): Completable {
        return FireConstants.deletedMessages.child(FireManager.uid).child(messageId).setValueRx(null)
    }

    private fun deleteNewGroupEvent(groupId: String): Completable {
        return FireConstants.newGroups.child(FireManager.uid).child(groupId).setValueRx(null)
    }

    override fun onResume() {
        super.onResume()
        if (enablePresence()) {
            presenceUtil?.onResume()
            MyApp.baseActivityResumed()
        }

        (this.application as? MyApp)?.let { application ->
            if (application.isHasMovedToForeground && SharedPreferencesManager.isFingerprintLockEnabled()) {

                val lastActive = SharedPreferencesManager.getLastActive()
                val lockAfter = SharedPreferencesManager.getLockAfter()


                if (lockAfter == 0 || TimeHelper.isTimePassedByMinutes(System.currentTimeMillis(), lastActive, lockAfter))
                    startActivity(Intent(this, LockscreenActivity::class.java))


            }
        }

    }


    override fun onPause() {
        super.onPause()
        if (enablePresence()) {
            presenceUtil?.onPause()
            MyApp.baseActivityPaused()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        presenceUtil?.onDestroy()

    }


}