# minecraft-advancednbttooltip
Minecraft Mod (Fabric Modloader) for 1.16.1 and above.

## How to install
To use this mod u must first [install fabric](https://fabricmc.net/wiki/install)<br>
and then [add the mod](https://fabricmc.net/wiki/tutorial:adding_mods)

## Custom JSON Tooltips (since 1.5.0)</h1>

Tooltips are automatically registered through resource packs in the folder "assets/<i>yournamespace</i>/tooltip/" with *.json* files. All tooltips the mod itself specifies can be modified through this method. The namespace to do that is "<i>advancednbttooltip</i>"

The root object requires a <b>"text"</b> and a <b>"condition"</b>.<br>
### TooltipFactories
Everywhere where "text" or "texts" is required a *TooltipFactory* can be used (There are some cases where the parameter might be called differently but in that case the reference explicitly uses a TooltipFactory there). This is one of <b>"literal", "formatted", "translated", "nbt", "nbt_size", "conditional", "mix", "multiple", "effect", "limit" or "limit_lines"</b>.<br>
### TooltipConditions
A *TooltipCondition* defines whether the tooltip will be shown at any moment. This is one of <b>"true", "false", "not", "and", "or", "is_item", "has_tag", "tag_matches", "is_advanced_context" or "is_hud_context"</b>.<br> 

### References

To see the specifics and all parameters of the TooltipFactories and TooltipConditions you can check the Learning Tooltips resource pack which you can play around with to get a feel for the json structure. To see how some things might be used there also is a resource pack mirroring the normal built-in Tooltips of the mod.<br>

[Learning Tooltips Pack](https://www.dropbox.com/s/jrmgt9birmzrppe/Learning-Tooltips.zip?dl=1 "Learning Tooltips Pack (Dropbox)")<br>
[Standard Tooltips Pack](https://www.dropbox.com/s/hrfne60k77bpjh7/Standard-Tooltips.zip?dl=1 "Standard Tooltips Pack (Dropbox)")
