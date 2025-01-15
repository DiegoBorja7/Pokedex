package ec.edu.uce.pokedex.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ability {

    @JsonProperty("is_hidden")
    private boolean isHidden;

    private int slot;

    private AbilityDetail ability;

    // Getters y setters
    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public AbilityDetail getAbility() {
        return ability;
    }

    public void setAbility(AbilityDetail ability) {
        this.ability = ability;
    }
}
