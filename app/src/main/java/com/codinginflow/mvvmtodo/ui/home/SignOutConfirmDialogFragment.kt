package com.codinginflow.mvvmtodo.ui.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

import com.codinginflow.mvvmtodo.ui.auth.LoginActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignOutConfirmDialogFragment : DialogFragment() {
    private lateinit var auth: FirebaseAuth


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        auth = Firebase.auth

        return AlertDialog.Builder(requireContext())
            .setTitle("Confirm sign out")
            .setMessage("Do you really want to sign out?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)

            }
            .create()
    }
}