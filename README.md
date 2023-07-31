<table  align="center">
	<tbody>
		<tr>
			<td width="280px" style="text-align: center;"><img src="https://github.com/paulevsGitch/VanillaBlockEnhancements/blob/main/src/main/resources/assets/vbe/icon.png"/></td>		
			<td>
				<h2 align="left">Vanilla Block Enhancements</h2>
				<a href="https://jitpack.io/#paulevsGitch/VanillaBlockEnhancements"><img src="https://jitpack.io/v/paulevsGitch/VanillaBlockEnhancements.svg"></a>
				<p>
					This mod changes behaviour for several vanilla blocks (like stairs and slabs)
					and fixes several bugs related to them.
				</p>
				<p>
					Dependencies:
					<ul>
						<li><a href="https://github.com/babric/prism-instance">Babric Instance (MultiMC/PolyMC/Prism)</a></li>
						<li><a href="https://jenkins.glass-launcher.net/job/StationAPI">StationAPI</a></li>
					</ul>
				</p>
			</td>		
		</tr>
	</tbody>
</table>

**Mod is not compatible with saves that have chests, doors and other blocks changed by that mod!**

**Make backup of your world before install!**

### Block Behaviour Changes:
- **Intractable blocks**: placing blocks with shift click will not open GUI or trigger them, digging will not activate them
- **Workbenches**: don't drop items when you close them and put them into your inventory instead
- **Chests**: use player direction instead of wall check, can be placed near each other
- **Doors**: no open/close issues, double doors will open at once, tag for doors that require redstone (`requires_power`)
- **Trapdoors**: no open/close issues, not require handle block, can be place on top and bottom, tag for trapdoors that require redstone (`requires_power`)
- **Slabs**: can be placed vertically, full slabs have directions, full slabs will break by parts, will be crafted 6 instead of 3
- **Pumpkins** and **Jack o' Lanterns**: don't require block below them
- **Fences**: can connect to solid blocks and blocks with `fence_connect` tag, have better collisions and selections
- **Water**: fixed bug when water was not created between two blocks if block below was water
- **Lava**: in the Nether 4 blocks of lava can produce one new between them, similar to 2 blocks of water
- **Stairs**: can be placed vertically and upside-down, will be crafted 6 instead of 4
- **Logs**: can be placed directionally
- **Leaves**: player placed leaves don't decay, natural leaves decay faster, all leaves have `leaves` tag, all logs with `logs` tag can support leaves, less transparent leaves
- **Beds**: monsters will not spawn near you randomly during night
- **Farmland**: not breaking on entity walking
- **Block Items**: no upscaling for not full blocks

Tools are now effective on some vanilla blocks (that was breaking slowly) and on all new blocks