/*
The MIT License

Copyright (c) 2026 DigitalWorldPure

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package com.goldenankh.data.repository

import com.goldenankh.data.dao.SampleDao
import com.goldenankh.data.entity.RGBColorEntity
import com.goldenankh.data.entity.SampleEntity
import com.goldenankh.domain.model.common.RGBColor
import com.goldenankh.domain.model.sample.Sample
import com.goldenankh.domain.model.sample.SampleIcon
import com.goldenankh.domain.repository.SampleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SampleRepositoryImpl @Inject constructor(
    private val dao: SampleDao
) : SampleRepository {

    override fun getCount(): Flow<Int> = dao.getCount()

    override fun observeAll(): Flow<List<Sample>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Sample?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun save(sample: Sample) {
        if (sample.id == 0L) {
            dao.insert(sample.toEntity())
        } else {
            dao.update(sample.toEntity())
        }
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    private fun SampleEntity.toDomain() = Sample(
        id = id,
        name = name,
        icon = SampleIcon.fromStorageValue(icon),
        color = with(color) { RGBColor(r, g, b) },
        program = program,
        durationSeconds = durationSeconds
    )

    private fun Sample.toEntity() = SampleEntity(
        id = id,
        name = name,
        icon = icon?.id,
        color = with(color) { RGBColorEntity (r, g, b) },
        program = program,
        durationSeconds = durationSeconds
    )
}
