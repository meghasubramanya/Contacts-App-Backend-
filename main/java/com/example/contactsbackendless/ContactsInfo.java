package com.example.contactsbackendless;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class ContactsInfo extends AppCompatActivity {


    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    TextView tvChar,tvName;
    ImageView ivCall,ivMail,ivEdit,ivDelete;
    EditText etName,etMail,etTelNr;
    Button btnSubmit;

    Boolean edit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_info);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        tvChar=findViewById(R.id.tvChar);
        tvName=findViewById(R.id.tvName);

        ivCall=findViewById(R.id.ivCall);
        ivMail=findViewById(R.id.ivMail);
        ivEdit=findViewById(R.id.ivEdit);
        ivDelete=findViewById(R.id.ivDelete);
        etName=findViewById(R.id.etName);
        etMail=findViewById(R.id.etMail);
        etTelNr=findViewById(R.id.etTelNr);
        btnSubmit=findViewById(R.id.btnSubmit);

        etName.setVisibility(View.GONE);
        etMail.setVisibility(View.GONE);
        etTelNr.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);

        final int index=getIntent().getIntExtra("index",0);

        etName.setText(ApplicationClass.contacts.get(index).getName());
        etMail.setText(ApplicationClass.contacts.get(index).getEmail());
        etTelNr.setText(ApplicationClass.contacts.get(index).getNumber());

        tvChar.setText(ApplicationClass.contacts.get(index).getName().toUpperCase().charAt(0)+"");
        tvName.setText(ApplicationClass.contacts.get(index).getName());

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uri="tel:" +ApplicationClass.contacts.get(index).getNumber();
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        ivMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL,ApplicationClass.contacts.get(index).getEmail());
                startActivity(Intent.createChooser(intent,
                       "send mail to "+ ApplicationClass.contacts.get(index).getEmail() ));
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit=!edit;
                if(edit)
                {
                    etName.setVisibility(View.VISIBLE);
                    etMail.setVisibility(View.VISIBLE);
                    etTelNr.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }
                else
                {
                    etName.setVisibility(View.GONE);
                    etMail.setVisibility(View.GONE);
                    etTelNr.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.GONE);

                }
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder dialog=new AlertDialog.Builder(ContactsInfo.this);
                dialog.setMessage("Are you sure you want to delete the contact?");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        showProgress(true);
                        tvLoad.setText("Deleting contact...please wait...");

                        Backendless.Persistence.of(Contact.class).remove(ApplicationClass.contacts.get(index), new AsyncCallback<Long>() {
                            @Override
                            public void handleResponse(Long response) {

                                ApplicationClass.contacts.remove(index);
                                Toast.makeText(ContactsInfo.this, "Contact removed!", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                ContactsInfo.this.finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {

                                Toast.makeText(ContactsInfo.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etName.getText().toString().isEmpty()||etMail.getText().toString().isEmpty()
                ||etTelNr.getText().toString().isEmpty())
                {
                    Toast.makeText(ContactsInfo.this, "Please enter all details!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ApplicationClass.contacts.get(index).setName(etName.getText().toString().trim());
                    ApplicationClass.contacts.get(index).setNumber(etTelNr.getText().toString().trim());
                    ApplicationClass.contacts.get(index).setEmail(etMail.getText().toString().trim());

                    showProgress(true);
                    tvLoad.setText("Updating contact...please wait...");

                    Backendless.Persistence.save(ApplicationClass.contacts.get(index), new AsyncCallback<Contact>() {
                        @Override
                        public void handleResponse(Contact response) {

                            //if changed then character and name at the top should also be changed.
                            tvChar.setText(ApplicationClass.contacts.get(index).getName().toUpperCase().charAt(0)+"");
                            tvName.setText(ApplicationClass.contacts.get(index).getName());
                            Toast.makeText(ContactsInfo.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(ContactsInfo.this, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    });
                }
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
