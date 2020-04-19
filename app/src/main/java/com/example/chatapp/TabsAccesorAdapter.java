package com.example.chatapp;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatapp.Chatsfragment;
import com.example.chatapp.Contactsfragment;
import com.example.chatapp.Groupsfragment;

public class TabsAccesorAdapter extends FragmentPagerAdapter {
    public TabsAccesorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0:
                Chatsfragment chatsfragment=new Chatsfragment();
                return chatsfragment;
            case 2:
                Groupsfragment groupsfragment=new Groupsfragment();
                return groupsfragment;
            case 1:
                Contactsfragment contactsfragment=new Contactsfragment();
                return contactsfragment;
            case 3:
                RequestFragment requestFragment=new RequestFragment();
                return requestFragment;

            default:
                return null;



        }


    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int i) {
        switch (i)
        {
            case 0:
                return "Chats";
            case 1:
                return "Contacts";
            case 2:
                return "groups";
            case 3:
                return "Request";




        }

        return null;
    }
}
