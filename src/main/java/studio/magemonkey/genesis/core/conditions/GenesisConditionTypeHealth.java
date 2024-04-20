package studio.magemonkey.genesis.core.conditions;

import studio.magemonkey.genesis.core.GenesisBuy;
import studio.magemonkey.genesis.core.GenesisShopHolder;
import org.bukkit.entity.Player;

public class GenesisConditionTypeHealth extends GenesisConditionTypeNumber {

    @Override
    public double getNumber(GenesisBuy shopItem, GenesisShopHolder holder, Player p) {
        return p.getHealth();
    }

    @Override
    public boolean dependsOnPlayer() {
        return true;
    }

    @Override
    public String[] createNames() {
        return new String[]{"health"};
    }


    @Override
    public void enableType() {
    }


}
