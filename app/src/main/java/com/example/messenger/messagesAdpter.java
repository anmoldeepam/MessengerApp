
package com.example.messenger;

import static com.example.messenger.chatwindo.reciverIImg;
import static com.example.messenger.chatwindo.senderImg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class messagesAdpter extends RecyclerView.Adapter {
    Context context;
    ArrayList<msgModelclass> messagesAdpterArrayList;
    String senderRoom;
    int ITEM_SEND=1;
    int ITEM_RECIVE=2;

    public messagesAdpter(Context context, ArrayList<msgModelclass> messagesAdpterArrayList,String senderRoom) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
        this.senderRoom = senderRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderVierwHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
            return new reciverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModelclass messages = messagesAdpterArrayList.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context).setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                DatabaseReference
                                        chatRef = FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("messages");
                                Log.d("DatabaseREFF",chatRef.toString());
                                chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                            String messageUID = messageSnapshot.getKey();
                                            DatabaseReference messageRef = chatRef.child(messageUID);
                                            Log.d("DatabaseREFF",messageRef.toString());

                                            messageRef.removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Message deleted successfully
                                                            Log.d("Delete", "Message deleted successfully");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Failed to delete message
                                                            Log.e("Delete", "Failed to delete message: " + e.getMessage());
                                                        }
                                                    });
                                            Log.d("Message UID", messageUID);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle error
                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                return false;
            }
        });
        if (holder.getClass()==senderVierwHolder.class){
            senderVierwHolder viewHolder = (senderVierwHolder) holder;
            String formattedDate = formatDate(messages.getTimeStamp());
            viewHolder.msgtxt.setText(formattedDate);
            viewHolder.timestp.setText(messages.getMessage());
            Picasso.get().load(senderImg).into(viewHolder.circleImageView);
        }else { reciverViewHolder viewHolder = (reciverViewHolder) holder;
            String formattedDate = formatDate(messages.getTimeStamp());
            viewHolder.msgtxt.setText(formattedDate);
            viewHolder.timestp.setText(messages.getMessage());
            Picasso.get().load(reciverIImg).into(viewHolder.circleImageView);


        }
    }
    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date(timestamp);
        String formattedTime = sdf.format(date);

        // Log the formatted timestamp
        Log.d("FormattedTime", "Formatted Time: " + formattedTime);
        return formattedTime;
    }

    @Override
    public int getItemCount() {
        return messagesAdpterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        msgModelclass messages = messagesAdpterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECIVE;
        }
    }

    class  senderVierwHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt,timestp;
        public senderVierwHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profilerggg);
            msgtxt = itemView.findViewById(R.id.msgsendertyp);
            timestp = itemView.findViewById(R.id.timestrap);

        }
    }
    class reciverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt,timestp;
        public reciverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.pro);
            msgtxt = itemView.findViewById(R.id.recivertextset);
            timestp = itemView.findViewById(R.id.timestrap);
        }
    }
}