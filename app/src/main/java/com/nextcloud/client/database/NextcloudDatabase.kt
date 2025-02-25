/*
 * Nextcloud Android client application
 *
 *  @author Álvaro Brey
 *  Copyright (C) 2022 Álvaro Brey
 *  Copyright (C) 2022 Nextcloud GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU AFFERO GENERAL PUBLIC LICENSE for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.nextcloud.client.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nextcloud.client.core.Clock
import com.nextcloud.client.core.ClockImpl
import com.nextcloud.client.database.dao.ArbitraryDataDao
import com.nextcloud.client.database.entity.ArbitraryDataEntity
import com.nextcloud.client.database.entity.CapabilityEntity
import com.nextcloud.client.database.entity.ExternalLinkEntity
import com.nextcloud.client.database.entity.FileEntity
import com.nextcloud.client.database.entity.FilesystemEntity
import com.nextcloud.client.database.entity.ShareEntity
import com.nextcloud.client.database.entity.SyncedFolderEntity
import com.nextcloud.client.database.entity.UploadEntity
import com.nextcloud.client.database.entity.VirtualEntity
import com.nextcloud.client.database.migrations.RoomMigration
import com.nextcloud.client.database.migrations.addLegacyMigrations
import com.owncloud.android.db.ProviderMeta

@Database(
    entities = [
        ArbitraryDataEntity::class,
        CapabilityEntity::class,
        ExternalLinkEntity::class,
        FileEntity::class,
        FilesystemEntity::class,
        ShareEntity::class,
        SyncedFolderEntity::class,
        UploadEntity::class,
        VirtualEntity::class
    ],
    version = ProviderMeta.DB_VERSION,
    exportSchema = true
)
@Suppress("Detekt.UnnecessaryAbstractClass") // needed by Room
abstract class NextcloudDatabase : RoomDatabase() {

    abstract fun arbitraryDataDao(): ArbitraryDataDao

    companion object {
        const val FIRST_ROOM_DB_VERSION = 65
        private var INSTANCE: NextcloudDatabase? = null

        @JvmStatic
        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated("Here for legacy purposes, inject this class or use getInstance(context, clock) instead")
        fun getInstance(context: Context): NextcloudDatabase {
            return getInstance(context, ClockImpl())
        }

        @JvmStatic
        fun getInstance(context: Context, clock: Clock): NextcloudDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room
                    .databaseBuilder(context, NextcloudDatabase::class.java, ProviderMeta.DB_NAME)
                    .allowMainThreadQueries()
                    .addLegacyMigrations(clock)
                    .addMigrations(RoomMigration())
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE!!
        }
    }
}
