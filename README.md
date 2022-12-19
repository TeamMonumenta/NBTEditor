NBTEditor
=========

NBTEditor is an in-game NBT editor for Bukkit/Minecraft servers such as Spigot
and Paper.

More information on the [NBTEditor BukkitDev page][NBTEditor].

This is a fork of https://github.com/goncalomb/NBTEditor maintained by the
Monumenta team. Monumenta is a community-developed free-to-play Minecraft
MMORPG - learn more at https://www.playmonumenta.com/.

Note that this fork is not API compatible with NBTEditor, and has diverged
significantly. As of this writing in 2022, the original NBTEditor has been
abandoned for years. It is unlikely that this fork will ever be reconciled with
the original NBTEditor should it return to active development. We will do our
best to develop a usable tool in the spirit of the original NBTEditor, but the
main priority is as a usuable tool for mob development for Monumenta.

Note that we have also developed a complementary plugin,
[Library of Souls][LibraryOfSouls], which integrates with NBTEditor to manage
hundreds or thousands of unique mobs across various world locations.


Download
--------

The latest version, 4.0, supports 1.16.4, 1.16.5, and 1.18.2.

Download it from [GitHub Packages][Packages]. The file you want is the one that
ends with ".jar".


Download
--------

## Maven dependency
```xml
    <repositories>
        <repository>
            <id>libraryofsouls</id>
            <url>https://raw.githubusercontent.com/TeamMonumenta/NBTEditor/master/mvn-repo/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.playmonumenta</groupId>
            <artifactId>nbteditor</artifactId>
            <version>4.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```

Note that the groupId was changed to clearly differentiate this maven artifact
from the original project, which is not directly compatible. Code files retain
their com.goncalomb.bukkit.nbteditor heritage.


Documentation / Help
--------------------

Check the [documentation on the Wiki][NBTEditor-Wiki].

Need help? Read the [FAQ][NBTEditor-FAQ], or join the
[Monumenta Discord][Monumenta-Discord] and ping `@Combustible#9141`


Contributing
------------

Contributions to the main branch are welcome. Keep the same code style and use
tabs for indentation.

Feel free to send suggestions and bug reports through Issues or on the
[Monumenta Discord][Monumenta-Discord]


License
-------

NBTEditor is available under the GPLv3 license, check [LICENSE.txt](LICENSE.txt).

[NBTEditor]: https://dev.bukkit.org/projects/nbteditor/
[NBTEditor-Wiki]: https://github.com/goncalomb/NBTEditor/wiki
[NBTEditor-FAQ]: https://github.com/goncalomb/NBTEditor/wiki/FAQ
[LibraryOfSouls]: https://github.com/TeamMonumenta/library-of-souls
[Monumenta-Discord]: https://discord.gg/monumenta
[Packages]: https://github.com/TeamMonumenta/NBTEditor/packages/1753006
