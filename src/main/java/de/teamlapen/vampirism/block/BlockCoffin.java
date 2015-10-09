package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffin;
import de.teamlapen.vampirism.util.IBlockRegistrable;
import de.teamlapen.vampirism.util.IIgnorePropsForRender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import org.eclipse.jdt.annotation.Nullable;

/**
 * 
 * @author Moritz
 *
 *         Metadata: first two bits are the direction third bit determines whether it is occupied or not (true means it's occupied) therefore if occupied: meta & 4 != 0 fourth bit determines whether
 *         it is the primary block (true means it's primary) therefore if primary: meta & -8 != 0
 */
public class BlockCoffin extends BasicBlockContainer  implements IIgnorePropsForRender{
	public static final String name = "blockCoffin";
	public final static Material material = Material.rock;
	private final String TAG = "BlockCoffin";

	public static final PropertyEnum PART = PropertyEnum.create("part", EnumPartType.class);

	@Override
	public IProperty[] getRenderIgnoredProperties() {
		return new IProperty[]{BlockBed.OCCUPIED};
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	public enum EnumPartType implements IStringSerializable{
		HEAD("head"),
		FOOT("foot");
		private final String name;
		EnumPartType(String name){
			this.name=name;
		}

		@Override
		public String getName() {
			return name;
		}


		@Override
		public String toString() {
			return getName();
		}
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	public BlockCoffin() {
		super(material, name);
		this.setCreativeTab(null);
		this.setDefaultState(this.blockState.getBaseState().withProperty(BlockBed.OCCUPIED, false).withProperty(PART, EnumPartType.FOOT));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumFacing = EnumFacing.getHorizontal(meta);
		return (meta&8)>0?this.getDefaultState().withProperty(PART,EnumPartType.HEAD).withProperty(FACING,enumFacing).withProperty(BlockBed.OCCUPIED,Boolean.valueOf((meta&4)>0)):this.getDefaultState().withProperty(PART, EnumPartType.FOOT).withProperty(FACING,enumFacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		byte b0 = 0;
		int i = b0 | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
		if(state.getValue(PART)==EnumPartType.HEAD){
			i|=8;
			if(((Boolean)state.getValue(BlockBed.OCCUPIED)).booleanValue()){
				i|=4;
			}
		}
		return i;
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this,new IProperty[]{BlockBed.OCCUPIED,PART,FACING});
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCoffin();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityCoffin te = (TileEntityCoffin) world.getTileEntity(pos);
		if (te == null)
			return;
		world.setBlockToAir(te.otherPos);
		world.removeTileEntity(te.otherPos);
		if (state.getValue(PART)==EnumPartType.HEAD)
			world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(ModItems.coffin, 1)));
		if(((Boolean)state.getValue(BlockBed.OCCUPIED)).booleanValue()) {
			wakeSleepingPlayer(world, pos);
		}
		super.breakBlock(world, pos, state);
	}


	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		if (state.getValue(PART) == EnumPartType.FOOT)
		{
			IBlockState iblockstate1 = worldIn.getBlockState(pos.offset((EnumFacing)state.getValue(FACING)));

			if (iblockstate1.getBlock() == this)
			{
				state = state.withProperty(BlockBed.OCCUPIED, iblockstate1.getValue(BlockBed.OCCUPIED));
			}
		}
		return state;
	}

	public EnumFacing getCoffinDirection(IBlockAccess world,BlockPos pos){
		return (EnumFacing) world.getBlockState(pos).getValue(FACING);
	}

	// Miscellaneous methods (rendertype etc.)
	@Override
	public int getMobilityFlag() {
		return 2;
	}

	@Override
	public boolean isBed(IBlockAccess world, BlockPos pos, Entity player) {
		return true;
	}


	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		} else {
			// Gets the coordinates of the primary block
			if (state.getValue(PART)==EnumPartType.FOOT) {
				TileEntityCoffin te = (TileEntityCoffin) world.getTileEntity(pos);
				pos=te.otherPos;
			}
			if (player.isSneaking()) {
				if(player.getCurrentEquippedItem()!=null&&player.getCurrentEquippedItem().getItem() instanceof ItemDye){
					return false;
				}

			}

			if (world.provider.canRespawnHere() && world.getBiomeGenForCoords(pos) != BiomeGenBase.hell) {
				if (((Boolean)state.getValue(BlockBed.OCCUPIED)).booleanValue()) {
					player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.coffin.occupied", new Object[0]));
					return true;
				}

				EntityPlayer.EnumStatus enumstatus = VampirePlayer.get(player).trySleepInCoffin(pos);

				if (enumstatus == EntityPlayer.EnumStatus.OK) {
					setCoffinOccupied(world,pos, player, true);
					((TileEntityCoffin) world.getTileEntity(pos)).markDirty();
					return true;
				} else {
					if (enumstatus == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
						player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.coffin.noSleep", new Object[0]));
					} else if (enumstatus == EntityPlayer.EnumStatus.NOT_SAFE) {
						player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe", new Object[0]));
					}
					return true;
				}
			} else{
				player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.coffin.wrong_dimension"));
				return true;
			}
		}
	}


	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		this.breakBlock(worldIn,pos,state);
	}


	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		TileEntityCoffin tileEntity = (TileEntityCoffin) world.getTileEntity(pos);
		if (tileEntity != null) {
			if (!(world.getBlockState(tileEntity.otherPos).getBlock() instanceof BlockCoffin)) {
				// Logger.d(TAG, "Other coffin block destroyed, removing this one");
				this.breakBlock(world, pos, state);
				// world.setBlockToAir(x, y, z);
				// world.removeTileEntity(x, y, z);
			}
		}
	}

	public static boolean isOccupied(IBlockAccess world,BlockPos pos){
		return (Boolean)world.getBlockState(pos).getValue(BlockBed.OCCUPIED);
	}

	public static void setCoffinOccupied(World world,BlockPos pos,boolean value){
		IBlockState state=world.getBlockState(pos);
		world.setBlockState(pos,state.withProperty(BlockBed.OCCUPIED,value),4);
	}


	public static boolean isHead(IBlockAccess world,BlockPos pos){
		return world.getBlockState(pos).getValue(PART)==EnumPartType.HEAD;
	}

	public void setCoffinOccupied(World world, BlockPos pos, @Nullable EntityPlayer player, boolean flag) {
		setBedOccupied(world, pos, player, flag);
		((TileEntityCoffin) world.getTileEntity(pos)).occupied = flag;
		// if(!world.isRemote)
		// ((EntityPlayerMP)
		// player).playerNetServerHandler.sendPacket(world.getTileEntity(x, y,
		// z).getDescriptionPacket());
	}

	private void wakeSleepingPlayer(World world, BlockPos pos) {
		if (world.isRemote)
			return;
		WorldServer w = (WorldServer) world;
		for (int i = 0; i < w.playerEntities.size(); i++) {
			EntityPlayer p = ((EntityPlayer) w.playerEntities.get(i));
			if (p.isPlayerSleeping()) {
				// Logger.d("BlockCoffin", String.format(
				// "Found sleeping player: x=%s, y=%s, z=%s",
				// p.playerLocation.posX, p.playerLocation.posY,
				// p.playerLocation.posZ));
				if (p.playerLocation.equals(pos)) {
					VampirePlayer.get(p).wakeUpPlayer(false, true, false, false);
				}
			}
		}
	}
}