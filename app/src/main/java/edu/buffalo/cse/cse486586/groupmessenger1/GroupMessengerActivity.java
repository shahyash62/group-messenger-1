package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.os.SystemClock.sleep;
import static edu.buffalo.cse.cse486586.groupmessenger1.GroupMessengerActivity.TAG;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static final int SERVER_PORT = 10000;

    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));


        final EditText editText = (EditText) findViewById(R.id.editText1);

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        try {
            /*
             * Create a server socket as well as a thread (AsyncTask) that listens on the server
             * port.
             *
             * AsyncTask is a simplified thread construct that Android provides. Please make sure
             * you know how it works by reading
             * http://developer.android.com/reference/android/os/AsyncTask.html
             */
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            /*
             * Log is a good way to debug your code. LogCat prints out all the messages that
             * Log class writes.
             *
             * Please read http://developer.android.com/tools/debugging/debugging-projects.html
             * and http://developer.android.com/tools/debugging/debugging-log.html
             * for more information on debugging.
             */
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }

        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String msg = editText.getText().toString() + "\n";
                editText.setText("");
                    /*
                    editText.setText(""); // This is one way to reset the input box.
                    TextView localTextView = (TextView) findViewById(R.id.local_text_display);
                    localTextView.append("\t" + msg); // This is one way to display a string.
                    TextView remoteTextView = (TextView) findViewById(R.id.remote_text_display);
                    remoteTextView.append("\n");
                    */

                    /*
                     * Note that the following AsyncTask uses AsyncTask.SERIAL_EXECUTOR, not
                     * AsyncTask.THREAD_POOL_EXECUTOR as the above ServerTask does. To understand
                     * the difference, please take a look at
                     * http://developer.android.com/reference/android/os/AsyncTask.html
                     */
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    /*
                     * If the key is pressed (i.e., KeyEvent.ACTION_DOWN) and it is an enter key
                     * (i.e., KeyEvent.KEYCODE_ENTER), then we display the string. Then we create
                     * an AsyncTask that sends the string to the remote AVD.
                     */
                    String msg = editText.getText().toString() + "\n";
                    editText.setText("");
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }


    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            Log.i(TAG, "doInBackground: Connecting to Socket");
            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */
            ServerSocket serverSocket = sockets[0];
            String message = null;
            Socket clientSocket = null;
            DataInputStream msgIn;
            Log.i(TAG, "doInBackground: Connecting to Socket");
            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */
            int count = 0;
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.authority("edu.buffalo.cse.cse486586.groupmessenger1.provider");
            uriBuilder.scheme("content");
            Uri mUri = uriBuilder.build();
            ContentResolver mContentResolver = getContentResolver();
            try {
                /*InputStream istream = clientSocket.getInputStream();
                BufferedReader msgIn = new BufferedReader(new InputStreamReader(istream));*/
                clientSocket = serverSocket.accept();
                msgIn = new DataInputStream(clientSocket.getInputStream());
                Log.i(TAG, "doInBackground: Working fine till here");
                while((message = msgIn.readUTF()) != null) {
                    Log.i(TAG, "doInBackground: in the loop");
                    Log.i(TAG, "doInBackground: Received the message: " + message);
                    ContentValues mNewValues = new ContentValues();
                    mNewValues.put("key", Integer.toString(count));
                    mNewValues.put("value", message);
                    mContentResolver.insert(mUri, mNewValues);
                    clientSocket = serverSocket.accept();
                    msgIn = new DataInputStream(clientSocket.getInputStream());
                    count++;
                }

                Log.i(TAG, "doInBackground: Loop exited");
            } catch (IOException e) {
                Log.i(TAG, "doInBackground: Exception in reading message");
            }

            Log.i(TAG, "Recieved message: " + message);
            return null;
        }
    }

    /***
     * ClientTask is an AsyncTask that should send a string over the network.
     * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
     * an enter key press event.
     *
     * @author stevko
     *
     */
    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {
            DataOutputStream msgOut;
            Log.i(TAG, "doInBackground: Trying to send message");
            try {
                String remotePort0 = REMOTE_PORT0, remotePort1 = REMOTE_PORT1, remotePort2 = REMOTE_PORT2, remotePort3 = REMOTE_PORT3, remotePort4 = REMOTE_PORT4;
                /*if (msgs[1].equals(REMOTE_PORT0)) {
                    remotePort0 = REMOTE_PORT1;
                    remotePort1 = REMOTE_PORT2;
                    remotePort2 = REMOTE_PORT3;
                    remotePort3 = REMOTE_PORT4;
                }

                if (msgs[1].equals(REMOTE_PORT1)) {
                    remotePort0 = REMOTE_PORT0;
                    remotePort1 = REMOTE_PORT2;
                    remotePort2 = REMOTE_PORT3;
                    remotePort3 = REMOTE_PORT4;
                }

                if (msgs[1].equals(REMOTE_PORT2)) {
                    remotePort0 = REMOTE_PORT0;
                    remotePort1 = REMOTE_PORT1;
                    remotePort2 = REMOTE_PORT3;
                    remotePort3 = REMOTE_PORT4;
                }

                if (msgs[1].equals(REMOTE_PORT3)) {
                    remotePort0 = REMOTE_PORT0;
                    remotePort1 = REMOTE_PORT1;
                    remotePort2 = REMOTE_PORT2;
                    remotePort3 = REMOTE_PORT4;
                }

                if (msgs[1].equals(REMOTE_PORT4)) {
                    remotePort0 = REMOTE_PORT0;
                    remotePort1 = REMOTE_PORT1;
                    remotePort2 = REMOTE_PORT2;
                    remotePort3 = REMOTE_PORT3;
                }*/

                Socket socket0 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(remotePort0));
                Socket socket1 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(remotePort1));
                Socket socket2 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(remotePort2));
                Socket socket3 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(remotePort3));
                Socket socket4 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(remotePort4));
                Log.i(TAG, "doInBackground: ClientTask Socket established");
                String msgToSend = msgs[0];
                /*
                 * TODO: Fill in your client code that sends out a message.
                 */
                msgOut = new DataOutputStream(socket0.getOutputStream());
                msgOut.writeUTF(msgToSend);
                msgOut.flush();

                msgOut = new DataOutputStream(socket1.getOutputStream());
                msgOut.writeUTF(msgToSend);
                msgOut.flush();

                msgOut = new DataOutputStream(socket2.getOutputStream());
                msgOut.writeUTF(msgToSend);
                msgOut.flush();

                msgOut = new DataOutputStream(socket3.getOutputStream());
                msgOut.writeUTF(msgToSend);
                msgOut.flush();

                msgOut = new DataOutputStream(socket4.getOutputStream());
                msgOut.writeUTF(msgToSend);
                msgOut.flush();

                Log.i(TAG, "Message sent " + msgToSend);

                //socket.close();
            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
            }

            return null;
        }
    }
}

