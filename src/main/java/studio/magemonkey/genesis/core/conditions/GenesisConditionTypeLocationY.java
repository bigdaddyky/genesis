package studio.magemonkey.genesis.core.conditions;


import org.bukkit.entity.Player;
import studio.magemonkey.genesis.core.GenesisBuy;
import studio.magemonkey.genesis.core.GenesisShopHolder;

public class GenesisConditionTypeLocationY extends GenesisConditionTypeNumber {

    @Override
    public double getNumber(GenesisBuy shopItem, GenesisShopHolder holder, Player p) {
        return p.getLocation().getY();
    }

    @Override
    public boolean dependsOnPlayer() {
        return true;
    }

    @Override
    public String[] createNames() {
        return new String[]{"locationy", "y"};
    }


    @Override
    public void enableType() {
    }


}
