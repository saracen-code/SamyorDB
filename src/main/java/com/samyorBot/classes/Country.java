package com.samyorBot.classes;

public class Country {
    // core properties
    private String successionType = "Not set yet";
    private long   population     = 0L;
    private double growthRate     = 0.0;
    private long   popCapacity    = 0L;
    private String mainMarket     = "Not set yet";
    private String currency       = "Not set yet";
    private double budget         = 0.0;

    // administration block
    private int nobility      = 0;
    private int institutions  = 0;
    private int landowners    = 0;
    private int burghers      = 0;
    private int peasants      = 0;
    private int tribes        = 0;
    private int bondmen       = 0;

    // final two properties
    private double devastation     = 0.0;
    private double centralization  = 0.0;

    public Country() {}

    public Country(String successionType,
                   long population,
                   double growthRate,
                   long popCapacity,
                   String mainMarket,
                   String currency,
                   double budget,
                   int nobility,
                   int institutions,
                   int landowners,
                   int burghers,
                   int peasants,
                   int tribes,
                   int bondmen,
                   double devastation,
                   double centralization)
    {
        this.successionType   = successionType;
        this.population       = population;
        this.growthRate       = growthRate;
        this.popCapacity      = popCapacity;
        this.mainMarket       = mainMarket;
        this.currency         = currency;
        this.budget           = budget;
        this.nobility         = nobility;
        this.institutions     = institutions;
        this.landowners       = landowners;
        this.burghers         = burghers;
        this.peasants         = peasants;
        this.tribes           = tribes;
        this.bondmen          = bondmen;
        this.devastation      = devastation;
        this.centralization   = centralization;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────────
    public String getSuccessionType() { return successionType; }
    public void setSuccessionType(String successionType) { this.successionType = successionType; }

    public long getPopulation() { return population; }
    public void setPopulation(long population) { this.population = population; }

    public double getGrowthRate() { return growthRate; }
    public void setGrowthRate(double growthRate) { this.growthRate = growthRate; }

    public long getPopCapacity() { return popCapacity; }
    public void setPopCapacity(long popCapacity) { this.popCapacity = popCapacity; }

    public String getMainMarket() { return mainMarket; }
    public void setMainMarket(String mainMarket) { this.mainMarket = mainMarket; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public int getNobility() { return nobility; }
    public void setNobility(int nobility) { this.nobility = nobility; }

    public int getInstitutions() { return institutions; }
    public void setInstitutions(int institutions) { this.institutions = institutions; }

    public int getLandowners() { return landowners; }
    public void setLandowners(int landowners) { this.landowners = landowners; }

    public int getBurghers() { return burghers; }
    public void setBurghers(int burghers) { this.burghers = burghers; }

    public int getPeasants() { return peasants; }
    public void setPeasants(int peasants) { this.peasants = peasants; }

    public int getTribes() { return tribes; }
    public void setTribes(int tribes) { this.tribes = tribes; }

    public int getBondmen() { return bondmen; }
    public void setBondmen(int bondmen) { this.bondmen = bondmen; }

    public double getDevastation() { return devastation; }
    public void setDevastation(double devastation) { this.devastation = devastation; }

    public double getCentralization() { return centralization; }
    public void setCentralization(double centralization) { this.centralization = centralization; }

    @Override
    public String toString() {
        return "Country{" +
                "successionType='" + successionType + '\'' +
                ", population=" + population +
                ", growthRate=" + growthRate +
                ", popCapacity=" + popCapacity +
                ", mainMarket='" + mainMarket + '\'' +
                ", currency='" + currency + '\'' +
                ", budget=" + budget +
                ", nobility=" + nobility +
                ", institutions=" + institutions +
                ", landowners=" + landowners +
                ", burghers=" + burghers +
                ", peasants=" + peasants +
                ", tribes=" + tribes +
                ", bondmen=" + bondmen +
                ", devastation=" + devastation +
                ", centralization=" + centralization +
                '}';
    }
}
