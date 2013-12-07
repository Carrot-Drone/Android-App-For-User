
package com.lchpartners.shadal;

import java.io.IOException;
import java.io.InputStream;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 * <p>This class is used by the {@link CardFlipActivity} and {@link
 * ScreenSlideActivity} samples.</p>
 */
public class ScreenSlidePageFragment extends Fragment{
    public static final String ARG_PAGE = "page";
    
    public static Context context;
    
    public static int imgCount;
    public static String phoneNumber;
    
    public static int mPageNumber;


    public static ScreenSlidePageFragment create(int pageNumber) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);
        
        //Set Image
        ImageView mImage = (ImageView) rootView.findViewById(R.id.imageView1);
		
        InputStream istream = null;
        if(imgCount == 1){
        	try {
        		istream = context.getAssets().open(phoneNumber+".jpg");
        	}catch(IOException e){
        		try{
        			istream = context.getAssets().open(phoneNumber+".png");
        		}catch(IOException e2){
        			System.out.println("Flyer not exist");
        		}
        	}
        }else if(imgCount == 2){
        	try {
        		istream = context.getAssets().open(phoneNumber+"_"+(mPageNumber+1)+".jpg");
        	}catch(IOException e){
        		try{
        			istream = context.getAssets().open(phoneNumber+"_"+(mPageNumber+1)+".png");
        		}catch(IOException e2){
        			System.out.println("Flyer not exist");
        		}
        	}
        }
        
	    Drawable d = Drawable.createFromStream(istream, null);
	    mImage.setImageDrawable(d);
	    
	    return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
