/*  Mod for minecraft about souls and feelings
    Copyright (C) 2022 by Hunkarada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.*/
package hunkarada.soulary.common.will;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;


public abstract class BasicWill {
    protected String id;
    protected ResourceLocation model;
    protected ResourceLocation texture;
    protected ResourceLocation sound;
    protected Entity caster;
    protected double willCost;
    protected double stabilityCost;
    protected double feelCost;
    protected abstract void cast();
    protected abstract void clientCast();

}
