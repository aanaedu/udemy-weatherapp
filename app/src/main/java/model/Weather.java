package model;

/**
 * Created by ANTHONY on 28/03/2016.
 */
public class Weather {
    public Location location;
    public String iconData;
    public CurrentCondition currentCondition = new CurrentCondition();
    public Temperature temperature = new Temperature();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Clouds clouds = new Clouds();


}
