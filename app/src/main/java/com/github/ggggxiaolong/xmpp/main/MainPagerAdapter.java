package com.github.ggggxiaolong.xmpp.main;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.ggggxiaolong.xmpp.R;
import com.github.ggggxiaolong.xmpp.utils.ObjectHolder;

/**
 * @author mrtan on 10/4/16.
 */

final class MainPagerAdapter extends FragmentStatePagerAdapter {
    private static int[] icons = new int[]{R.drawable.selector_tab_chat,R.drawable.selector_tab_contact,R.drawable.selector_tab_found,R.drawable.selector_tab_me};
    private static int[] title = new int[]{R.string.prompt_tab_chat,R.string.prompt_tab_contact,R.string.prompt_tab_found,R.string.prompt_tab_me};

    MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:{
                return new SessionFragment();
            }
            case 1:{
                return new ContactFragment();
            }
            default:{
                return new TempFragment();
            }
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ObjectHolder.context.getString(title[position]);
    }

    static void setTabIcon(TabLayout tabLayout){
        int tabCount = tabLayout.getTabCount();
        if (tabCount == icons.length){
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                tab.setIcon(icons[i]);
            }
        }
    }
}
