package jerklib;


/**
 * @author mohadib
 *         <p/>
 *         Used when get a new instance of connection mananer.
 *         Connection manager uses this information when making new connections
 */
public class ProfileImpl implements Profile {
    private String name, actualNick, firstNick, secondNick, thirdNick;

    /**
     * @param name       Username
     * @param nick       Nick
     * @param secondNick Alt nick 1
     * @param thirdNick  Alt nick 2
     */
    public ProfileImpl(String name, String nick, String secondNick, String thirdNick) {
        this.name = name == null ? "" : name;
        this.firstNick = nick == null ? "" : nick;
        this.secondNick = secondNick == null ? "" : secondNick;
        this.thirdNick = thirdNick == null ? "" : thirdNick;
        actualNick = firstNick;
    }

    /* (non-Javadoc)
      * @see jerklib.Profile#getName()
      */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
      * @see jerklib.Profile#getFirstNick()
      */
    public String getFirstNick() {
        return firstNick;
    }

    /* (non-Javadoc)
      * @see jerklib.Profile#getSecondNick()
      */
    public String getSecondNick() {
        return secondNick;
    }

    /* (non-Javadoc)
      * @see jerklib.Profile#getThirdNick()
      */
    public String getThirdNick() {
        return thirdNick;
    }

    /* (non-Javadoc)
      * @see jerklib.Profile#getActualNick()
      */
    public String getActualNick() {
        return actualNick;
    }

    public void setActualNick(String aNick) {
        actualNick = aNick;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        if (actualNick != null ? !actualNick.equals(profile.getActualNick()) : profile.getActualNick() != null)
            return false;
        if (name != null ? !name.equals(profile.getName()) : profile.getName() != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (actualNick != null ? actualNick.hashCode() : 0);
        return result;
    }

    @Override
    public Profile clone() throws CloneNotSupportedException {
        ProfileImpl impl = new ProfileImpl(name,
                firstNick,
                secondNick,
                thirdNick);
        impl.setActualNick(actualNick);
        return impl;
    }
}
