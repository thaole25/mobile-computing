package com.example.restaurantrecognition.ui.help;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.restaurantrecognition.R;
import com.google.rpc.Help;

public class HelpFragment extends Fragment {

    private HelpViewModel helpViewModel;
    Button sendMessageButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        helpViewModel =
                ViewModelProviders.of(this).get(HelpViewModel.class);
        View root = inflater.inflate(R.layout.fragment_help, container, false);
        final TextView textView = root.findViewById(R.id.text_help);
        helpViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        // Send a message to an email
        sendMessageButton = root.findViewById(R.id.btnSendMessage);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d("Error", "Pressing button" );
                final EditText editTextName = root.findViewById(R.id.editTextName);
                String name = editTextName.getText().toString();
                final EditText editTextEmail= root.findViewById(R.id.editTextEmail);
                String email = editTextEmail.getText().toString();
                final EditText editTextMessage= root.findViewById(R.id.editTextMessage);
                String message = editTextMessage.getText().toString();
                final EditText editTextPhone= root.findViewById(R.id.editTextPhone);
                String phone = editTextPhone.getText().toString();

                String messageBody=
                                "Name: "+name+
                                "<br>Phone: "+phone +
                                "<br>Email: "+email + "<br>"+
                                "<br>Message: "+message;
                Intent messageIntent = new Intent(Intent.ACTION_SEND);
                messageIntent.setType("message/rfc822");
                messageIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"gegarciam95@gmail.com"});
                messageIntent.putExtra(Intent.EXTRA_SUBJECT, "Help and Feedback: Restaurant Lens");
                messageIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(messageBody));
                try {
                    startActivity(Intent.createChooser(messageIntent, "Sending email ..."));
                    editTextName.setText("");
                    editTextEmail.setText("");
                    editTextMessage.setText("");
                    editTextPhone.setText("");
                } catch (android.content.ActivityNotFoundException exception) {
                    Toast.makeText(HelpFragment.this.getActivity(), "Error: No email on device", Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;
    }

}