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

### Block Behaviour Changes:
- Slabs: can be placed vertically, full slabs have directions, full slabs will break by parts
- Pumpkins and Jack o' Lanterns: don't require block below them

*Planned:*
- Stairs: can be placed vertically and upside-down
- Furnaces: use player direction instead of wall check
- Chests: use player direction instead of wall check, can be placed near each other
- Trapdoors: not require handle block, can be place on top and bottom
- Doors: use blockstates (no open/close issues), double doors will open at once