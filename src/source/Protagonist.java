package source;

import javax.xml.bind.annotation.*;

import emotions.Emotions;

import static java.lang.Math.abs;

@XmlType(name = "Protagonist")
@XmlRootElement
public class Protagonist implements Comparable<Protagonist> {
    private String gender;
    private double Strength;
    private double Agility;
    private double Intelligence;
    private double Luck;
    private Location location;
    private String Name;
    private int wealth;
    private double LevelOfPain;
    private int ballCounter;
    private double Defence;
    private Emotions emotion;

    public Protagonist() {
    }

    public Protagonist(String gender,
                       double strength,
                       double agility,
                       double intelligence,
                       double luck,
                       Location location,
                       String name,
                       int wealth,
                       double levelOfPain,
                       int ballCounter,
                       double defence,
                       Emotions emotion) {
        this.gender = gender;
        Strength = strength;
        Agility = agility;
        Intelligence = intelligence;
        Luck = luck;
        this.location = location;
        Name = name;
        this.wealth = wealth;
        LevelOfPain = levelOfPain;
        this.ballCounter = ballCounter;
        Defence = defence;
        this.emotion = emotion;
    }

    @Override
    public int hashCode() {
        if (Name != null) {
            int s = abs((int) Strength * 10);
            int a = abs((int) Agility * 10);
            int i = abs((int) Intelligence * 10);
            int k = abs((int) LevelOfPain * 10);
            int l = Name.length();
            String str = "";
            str += s + a + i + k + l;
            return Integer.parseInt(str);
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Protagonist F = (Protagonist) obj;
        boolean semiResult = (this.Strength == F.Strength && this.Agility == F.Agility && this.Intelligence == F.Intelligence &&
                this.Luck == F.Luck && this.wealth == F.wealth && this.emotion == ((Protagonist) obj).emotion
                && this.LevelOfPain == F.LevelOfPain && this.ballCounter == F.ballCounter);
        boolean FName = false;
        boolean Fgender = false;
        boolean Flocation = false;
        boolean Femotion = false;
        if (Name == null) {
            if (F.Name == null) {
                FName = true;
            }
        } else {
            FName = Name.equals(F.Name);
        }
        if (gender == null) {
            if (F.gender == null) {
                Fgender = true;
            }
        } else {
            Fgender = gender.equals(F.gender);
        }
        if (location == null) {
            if (F.location == null) {
                Flocation = true;
            }
        } else {
            Flocation = location.equals(F.location);
        }
        if (emotion == null) {
            if (F.emotion == null) {
                Femotion = true;
            }
        } else {
            Femotion = emotion.equals(F.emotion);
        }
        return semiResult && Femotion && Fgender && Flocation && FName;
    }

    @Override
    public String toString() {
        return "Name = " + Name + ";\ngender = " + gender + ";\nStrength = " + Strength + ";\nAgility = " + Agility + ";\nIntelligence = " + Intelligence +
                ";\nLuck = " + Luck + ";\nwealth = " + wealth + ";\nLevelOfPain = "
                + LevelOfPain + ";\nballcounter = " + ballCounter + ";\nDefence = " + Defence + ";\n" +
                "emotion = " + emotion + ";\nlocation = " + location + ";";
    }

    @Override
    public int compareTo(Protagonist o) {
        if (o.Name != null && this.Name != null) {
            return this.Name.length() - o.Name.length();
        } else {
            return 1;
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

}