package com.dsh105.echopet.compat.api.entity.type.pet

import com.dsh105.echopet.compat.api.entity.IPet

interface IGuardianPet: IPet {
    var elder: Boolean
}