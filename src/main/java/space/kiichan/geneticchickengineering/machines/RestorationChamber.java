package space.kiichan.geneticchickengineering.machines;

import io.github.thebusybiscuit.cscorelib2.inventory.InvUtils;
import io.github.thebusybiscuit.cscorelib2.inventory.ItemUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import space.kiichan.geneticchickengineering.GeneticChickengineering;
import space.kiichan.geneticchickengineering.chickens.PocketChicken;
import space.kiichan.geneticchickengineering.items.GCEItems;

public class RestorationChamber extends AContainer  {
    private GeneticChickengineering plugin;
    private final PocketChicken pc;
    private int healRate;

    public RestorationChamber(GeneticChickengineering plugin, Category category, int healRate, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
        this.plugin = plugin;
        this.pc = plugin.pocketChicken;
        this.healRate = healRate;
    }

    @Override
    public ItemStack getProgressBar() {
        return GCEItems.POCKET_CHICKEN;
    }

    @Override
    public String getMachineIdentifier() {
        return "GCE_RESTORATION_CHAMBER";
    }

    @Override
    protected MachineRecipe findNextRecipe(BlockMenu inv) {
        ItemStack chick = null;
        ItemStack seeds = null;
        for (int slot : getInputSlots()) {
            ItemStack item = inv.getItemInSlot(slot);

            if (item == null) {
                continue;
            }

            if (this.pc.isPocketChicken(item)) {
                chick = item;
            } else if (item.getType() == Material.WHEAT_SEEDS) {
                seeds = item;
            }
        }

        if (chick == null || seeds == null) {
            return null;
        }

        double health = this.pc.getHealth(chick);
        int seedAmount = seeds.getAmount();
        int toConsume = 0;
        while (seedAmount > 0 && health < 4d) {
            seedAmount--;
            toConsume++;
            health = health + 0.25;
        }
        if (toConsume == 0) {
            return null;
        }
        ItemStack recipeSeeds = seeds.clone();
        recipeSeeds.setAmount(toConsume);
        ItemStack recipeChick = chick.clone();
        recipeChick.setAmount(1);
        this.plugin.heal(recipeChick, toConsume*0.25);
        MachineRecipe recipe = new MachineRecipe(this.healRate*toConsume, new ItemStack[] {recipeSeeds, chick.clone()}, new ItemStack[] {recipeChick});
        if (!InvUtils.fitAll(inv.toInventory(), recipe.getOutput(), getOutputSlots())) {
            return null;
        }
        ItemUtils.consumeItem(chick, false);
        ItemUtils.consumeItem(seeds, toConsume, false);
        return recipe;
    }
}
