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

import com.goldenankh.data.dao.CurveDao
import com.goldenankh.data.entity.CurveEntity
import com.goldenankh.domain.model.curve.Curve
import com.goldenankh.domain.repository.CurveRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CurveRepositoryImpl @Inject constructor(
    private val dao: CurveDao
) : CurveRepository {

    override fun observeCurves(): Flow<List<Curve>> {
        return dao.observeAll().map { entities ->
            entities.map {
                Curve(
                    id = it.id,
                    name = it.name,
                    data = it.data
                )
            }
        }
    }

    override fun observeCurve(id: Long): Flow<Curve?> {
        return dao.observeById(id).map { entity ->
            entity?.let {
                Curve(
                    id = it.id,
                    name = it.name,
                    data = it.data
                )
            }
        }
    }

    override suspend fun save(curve: Curve) {
        if (curve.id == 0L) {
            dao.insert(
                CurveEntity(
                    name = curve.name,
                    data = curve.data
                )
            )
        } else {
            dao.update(
                CurveEntity(
                    id = curve.id,
                    name = curve.name,
                    data = curve.data
                )
            )
        }
    }

    override suspend fun delete(id: Long) {
        dao.observeById(id).firstOrNull()?.let {
            dao.delete(it)
        }
    }
}