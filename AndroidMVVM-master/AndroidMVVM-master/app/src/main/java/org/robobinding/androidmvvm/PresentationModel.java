package org.robobinding.androidmvvm;

import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;


/**
 * @author Cheng Wei
 * @version $Revision: 1.0 $
 * @since 1.0
 */
@org.robobinding.annotation.PresentationModel
public class PresentationModel implements HasPresentationModelChangeSupport {
    private PresentationModelChangeSupport changeSupport;


    public PresentationModel() {
        changeSupport = new PresentationModelChangeSupport(this);
    }

    public String getHello() {
        return test + ": hello Android MVVM(Presentation Model)!";
    }
    public void sayHello() {
        changeSupport.firePropertyChange("hello");
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String test;
    public void setTest(String test){ this.test = test;  }
    public String getTest(){return test;}

    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }
}
