/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Created at 2:12:15 PM Jul 17, 2010
 */
package nars.lab.narclear.jbox2d;

import org.jbox2d.collision.Collision.PointState;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

/**
 * Contact point for {@link TestbedTest}.
 * @author Daniel Murphy
 */
public class ContactPoint {
	public Fixture fixtureA;
	public Fixture fixtureB;
	public final Vec2 normal = new Vec2();
	public final Vec2 position = new Vec2();
	public PointState state;
	public float normalImpulse;
	public float tangentImpulse;
	public float separation;
}
