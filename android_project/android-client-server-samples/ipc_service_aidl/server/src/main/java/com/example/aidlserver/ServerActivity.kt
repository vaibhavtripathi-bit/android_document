package com.example.aidlserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aidlserver.databinding.ActivityServerBinding

class ServerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.statusText.text = "AIDL BookManager Service is running.\n\nClient app can bind to this service to:\n• Get book list\n• Add books\n• Query by ID\n\nCheck Logcat (tag: BookManagerService) for IPC calls."
    }
}
