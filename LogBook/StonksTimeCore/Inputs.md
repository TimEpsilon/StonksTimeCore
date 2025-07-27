#graph #mods #SCT
# Problem

- There are ~1000 inputs.
- Most of them are on the main partition (part-000)
- Plenty of those are similar in some way
	- We could use tags but that would mean changing the current graph objects, which I don't want to do. And also getting tags out of NeoForge is annoying
	- => We can use name matching as a proxy

The rule of thumb is that the value of an item is based on it's scarcity and not how valuable it really is.

Remember that the min value is 1.

![[SCT#Currencies#Summary]]
# Comments

## Missing Recipe Types 

| Type                                      | Missing I/O | Total | TODO |
| ----------------------------------------- | ----------- | ----- | ---- |
| crafting                                  | 44          | 3800  |      |
| smithing                                  | 87          | 87    | X    |
| irons_spellbooks:alchemist_cauldron_empty | 16          | 16    | X    |
| create:sequenced_assembly                 | 27          | 27    | X    |
| create:mixing                             | 5           | 11    | X    |
| irons_spellbooks:alchemist_cauldron_brew  | 15          | 15    | X    |
| createoreexcavation:drilling              | 14          | 14    | X    |
| createoreexcavation:vein                  | 15          | 15    | X    |
| createoreexcavation:extracting            | 1           | 1     | X    |
| create:compacting                         | 2           | 8     | X    |
| irons_spellbooks:alchemist_cauldron_fill  | 16          | 16    | X    |

**create:compacting** : No inputs for liquids
**createoreexcavation** : No input / No output -> Ignore
**create:mixing** : No output for liquids

### crafting :
> - minecraft:firework_star {} {'minecraft:firework_star': 1} -> Ignore
> - minecraft:decorated_pot {} {} -> Ignore
> - minecraft:map_cloning {} {} -> Ignore
> - supplementaries:sus_gravel {} {} -> Ignore
> - kaleidoscope_doll:doll_entity_crafting {} {} -> Ignore
> - *supplementaries:present_dye* {} {}
> - supplementaries:flags/flag_from_banner {} {} -> Ignore
> - sophisticatedstorage:flat_top_barrel_toggle {} {} -> Ignore
> - randomium:randomium_clone {} {} -> Ignore
> - minecraft:tipped_arrow {} {} -> Ignore
> - supplementaries:item_lore {} {} -> Ignore
> - *create:crafting/curiosities/toolbox_dyeing* {} {}
> - supplementaries:bubble_blower_charge {} {} -> Ignore
> - minecraft:firework_star_fade {} {} -> Ignore
> - sophisticatedbackpacks:backpack_dye {} {} -> Ignore
> - supplementaries:antique_book {} {} -> Ignore
> - supplementaries:antique_map_clean {} {} -> Ignore
> - supplementaries:sus_bricks {} {} -> Ignore
> - supplementaries:rope_arrow_add {} {} -> Ignore
> - *minecraft:shulker_box_coloring* {} {}
> - *suppsquared:sack_dye* {} {}
> - supplementaries:sus_sand {} {} -> Ignore
> - minecraft:suspicious_stew {} {} -> Ignore
> - minecraft:shield_decoration {} {} -> Ignore
> - supplementaries:item_lore_clear {} {} -> Ignore
> - *supplementaries:trapped_present* {} {}
> - supplementaries:bamboo_spikes_tipped {} {} -> Ignore
> - supplementaries:soap/clear {} {} -> Ignore
> - amendments:dye_bottle {} {} -> Ignore
> - minecraft:armor_dye {} {} -> Ignore
> - *supplementaries:safe* {} {}
> - supplementaries:rope_arrow_create {} {} -> Ignore
> - minecraft:firework_rocket {} {'minecraft:firework_rocket': 1} -> Ignore
> - sophisticatedstorage:storage_dye {} {} -> Ignore
> - minecraft:banner_duplicate {} {} -> Ignore
> - supplementaries:antique_book_clean {} {} -> Ignore
> - create:crafting/curiosities/item_copying {} {} -> Ignore
> - minecraft:repair_item {} {} -> Ignore
> - sophisticatedstorage:barrel_material {} {} -> Ignore
> - chimes:glass_bells_custom {} {} -> Ignore
> - minecraft:book_cloning {} {} -> Ignore
> - supplementaries:antique_map {} {} -> Ignore
> - sophisticatedcore:upgrade_clear {} {} -> Ignore
> - supplementaries:blackboard_duplicate {} {} -> Ignore

****
## Errors
- [x] Netherite / Smithing table recipes not taken into account
	- [ ] Also do output
- [x] Liquids not taken into account
- [ ] Color switching not taken into account
- [x] Ignore every cardboard package
	- Except for rare ones
- [x] Crushed recipes not taken into account
- [x] Recipe sequences not taken into account
- [x] Not every chest is a lootr chest
- [ ] Add equivalency node
- [x] Too many structures
	- Install mod to know which structure
## Logs
20

- *betterarcheology:rotten_log* : Found in structure -> rare, drops sticks -> less valuable than logs => Value > other logs

## Leaves
1

## Saplings
5

## Create Rare package
Rarely obtained by making a package, should thus cost more
