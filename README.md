# minecraft-advancednbttooltip
Minecraft Mod (Fabric Modloader) for 1.16.1 and above.

<h1>Custom JSON Tooltips</h1>
<h2>Basic JSON Structure</h2>

CustomTooltip (JSON Root)<br>
>├── "id": the registry name of the tooltip<br>
>├── "text": a TooltipFactory object<br>
>└── "condition": a TooltipCondition object<br>

TooltipFactory <br>
>├── "id": the id of the built-in TooltipFactory<br>
>└── ... (specific to the id)<br>

TooltipCondition <br>
>├── "id": the id of the built-in TooltipCondition<br>
>└── ... (specific to the id)<br>

<h2>TooltipFactories</h2>

literal<br>
>├── all general TooltipFactory attributes<br>
>└── "text": a string of plaintext<br>

formatted<br>
>├── all general TooltipFactory attributes<br>
>├── "text": a TooltipFactory providing text to be formatted<br>
>├── "bold": (optional) a boolean that makes the provided text bold<br>
>├── "italic": (optional) a boolean that makes the provided text italic<br>
>├── "strikethrough": (optional) a boolean that makes the provided text strikethrough<br>
>├── "underline": (optional) a boolean that makes the provided text underlined<br>
>├── "obfuscated": (optional) a boolean that makes the provided text obfuscated<br>
>└── "color": (optional) a string of a minecraft color<br>

translated<br>
>├── all general TooltipFactory attributes<br>
>├── "key": a string containing the translation key that is to be translated<br>
>└── "argument_provider": (optional) a TooltipFactory which gives text for arguments in the translation as lines<br>

nbt<br>
>├── all general TooltipFactory attributes<br>
>├── "flags": (optional) 1st bit for going into compound tags, 2nd bit for going into list tags<br>
>└── "path": the path to the NBT-Tag<br>

conditional<br>
>├── all general TooltipFactory attributes<br>
>├── "condition": a TooltipCondition<br>
>├── "success": a TooltipFactory that provides the text if the condition is met<br>
>└── "fail": a TooltipFactory that provides the text if the condition is not met<br>

multiple<br>
>├── all general TooltipFactory attributes<br>
>└── "texts": a list of TooltipFactories to be added together<br>
>>└── a TooltipFactory<br>

mix<br>
>├── all general TooltipFactory attributes<br>
>└── "texts": a list of TooltipFactories to be added together horizontally<br>
>>└── a TooltipFactory<br>

empty<br>
>└── all general TooltipFactory attributes<br>

<h2>TooltipConditions</h2>

not<br>
>├── all general TooltipConditon attributes<br>
>└── "condition": a TooltipConditon to be negated<br>

and<br>
>├── all general TooltipConditon attributes<br>
>└── "conditions": a list of TooltipConditon to be AND-ed<br>
>>└── a TooltipConditon<br>

or<br>
>├── all general TooltipConditon attributes<br>
>└── "conditions": a list of TooltipConditon to be OR-ed<br>
>>└── a TooltipConditon<br>

has_tag<br>
>├── all general TooltipConditon attributes<br>
>├── "tag": the path to a NBT Tag<br>
>└── "type" (optional) a NBT Tag type as specified in the NBT Tag classes<br>

tag_matches<br>
>├── all general TooltipConditon attributes<br>
>├── "tag": the path to a NBT Tag<br>
>└── "value": the value the tag should have<br>

is_item<br>
>├── all general TooltipConditon attributes<br>
>└── "items": a list of item ids to check against<br>
>>└── an item id<br>

is_advanced_context<br>
>└── all general TooltipConditon attributes<br>

is_hud_context<br>
>└── all general TooltipConditon attributes<br>

true<br>
>└── all general TooltipConditon attributes<br>

false<br>
>└── all general TooltipConditon attributes<br>
