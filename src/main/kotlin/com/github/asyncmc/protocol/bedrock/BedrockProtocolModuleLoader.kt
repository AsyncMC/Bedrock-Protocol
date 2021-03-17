/*
 *  AsyncMC - A fully async, non blocking, thread safe and open source Minecraft server implementation
 *  Copyright (C) 2021  José Roberto de Araújo Júnior <joserobjr@gamemods.com.br>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.asyncmc.protocol.bedrock

import com.github.asyncmc.module.api.ContactInformation
import com.github.asyncmc.module.api.Module
import com.github.asyncmc.module.api.ModuleLoader
import com.github.asyncmc.module.api.Server
import kotlinx.coroutines.CoroutineScope
import java.net.URL

public class BedrockProtocolModuleLoader: ModuleLoader() {
    override val name: String
        get() = "Bedrock Edition Protocol"
    override val version: String
        get() = "0.1.0-SNAPSHOT"
    override val description: String
        get() = "Allows players to connect using Minecraft Bedrock Edition"
    override val authors: Set<ContactInformation>
        get() = setOf(ContactInformation("José Roberto de Araújo Júnior", "joserobjr@powernukkit.org"))
    override val sourceCode: URL
        get() = URL("https://github.com/AsyncMC/Bedrock-Protocol")

    override fun CoroutineScope.createModules(server: Server, secrets: LoadingSecrets): List<Module> {
        TODO("Not yet implemented")
    }
}
