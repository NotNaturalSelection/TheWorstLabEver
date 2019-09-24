package source;


import emotions.Emotions;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import static java.lang.Math.abs;

public class Protagonist implements Comparable<Protagonist>, Serializable {
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
    private LocalDateTime localDateTime;
    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Protagonist() {
        localDateTime = LocalDateTime.now();
    }

    public Protagonist(String gender,
                       String name,
                       double strength,
                       double agility,
                       double intelligence,
                       double luck,
                       int wealth,
                       int ballCounter,
                       double levelOfPain,
                       double defence,
                       LocalDateTime ldt,
                       Location location,
                       String owner) {


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
        this.owner = owner;
        if(ldt != null){
            localDateTime = ldt;
        } else {
            localDateTime = LocalDateTime.now();
        }
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
                this.Luck == F.Luck && this.wealth == F.wealth
                && this.LevelOfPain == F.LevelOfPain && this.ballCounter == F.ballCounter);
        boolean Fowner = false;
        boolean FName = false;
        boolean Fgender = false;
        boolean Flocation = false;
        if (Name == null) {
            if (F.Name == null) {
                FName = true;
            }
        } else {
            FName = Name.equals(F.Name);
        }
        if (owner == null) {
            if (F.owner == null) {
                FName = true;
            }
        } else {
            Fowner = owner.equals(F.owner);
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
        return semiResult && Fgender && Flocation && FName && Fowner ;
    }

    @Override
    public String toString() {
        return "Name = " + Name + ";\ngender = " + gender + ";\nStrength = " + Strength + ";\nAgility = " + Agility + ";\nIntelligence = " + Intelligence +
                ";\nLuck = " + Luck + ";\nwealth = " + wealth + ";\nLevelOfPain = "
                + LevelOfPain + ";\nballcounter = " + ballCounter + ";\nDefence = " + Defence +
                ";\nlocation = " + location + ";\n";
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

    public String getGender() {
        return gender;
    }

    public double getStrength() {
        return Strength;
    }

    public double getAgility() {
        return Agility;
    }

    public double getIntelligence() {
        return Intelligence;
    }

    public double getLuck() {
        return Luck;
    }

    public int getWealth() {
        return wealth;
    }

    public double getLevelOfPain() {
        return LevelOfPain;
    }

    public int getBallCounter() {
        return ballCounter;
    }

    public double getDefence() {
        return Defence;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}