/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stefang.app.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.stefang.app.core.database.DataItemType
import com.stefang.app.core.database.DataItemTypeDao
import javax.inject.Inject

interface DataItemTypeRepository {
    val dataItemTypes: Flow<List<String>>

    suspend fun add(name: String)
}

class DefaultDataItemTypeRepository @Inject constructor(
    private val dataItemTypeDao: DataItemTypeDao
) : DataItemTypeRepository {

    override val dataItemTypes: Flow<List<String>> =
        dataItemTypeDao.getDataItemTypes().map { items -> items.map { it.name } }

    override suspend fun add(name: String) {
        dataItemTypeDao.insertDataItemType(DataItemType(name = name))
    }
}
