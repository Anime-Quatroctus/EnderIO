package crazypants.enderio.item.skull;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.BlockEio;
import crazypants.enderio.IHaveRenderers;
import crazypants.enderio.ModObject;
import crazypants.util.ClientUtil;
import crazypants.util.NullHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "Thaumcraft")
public class BlockEndermanSkull extends BlockEio<TileEndermanSkull> implements IHaveRenderers { //TODO: 1.9 Thaumcraft IInfusionStabiliser

  public enum SkullType implements IStringSerializable {

    BASE("base", false),
    REANIMATED("reanimated", true),
    TORMENTED("tormented", false),
    REANIMATED_TORMENTED("reanimatedTormented", true);

    final @Nonnull String name;
    final boolean showEyes;

    SkullType(@Nonnull String name, boolean showEyes) {
      this.name = name;
      this.showEyes = showEyes;
    }

    @Override
    public @Nonnull String getName() {
      return NullHelper.notnullJ(name.toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
    }

    public static @Nonnull SkullType getTypeFromMeta(int meta) {
      return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
    }
  }

  public static final @Nonnull PropertyEnum<SkullType> VARIANT = PropertyEnum.<SkullType> create("variant", SkullType.class);

  public static BlockEndermanSkull create() {
    BlockEndermanSkull res = new BlockEndermanSkull();
    res.init();
    return res;
  }

  public static final @Nonnull AxisAlignedBB AABB = new AxisAlignedBB(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);

  private BlockEndermanSkull() {
    super(ModObject.blockEndermanSkull.getUnlocalisedName(), TileEndermanSkull.class, Material.CIRCUITS);
  }

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
    return AABB;
  }

  @Override
  protected ItemBlock createItemBlock() {
    return new ItemEndermanSkull(this, name);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    Item item = Item.getItemFromBlock(this);    
    int num = SkullType.values().length;
    for (int i = 0; i < num; i++) {
      SkullType st = SkullType.values()[i];
      ClientUtil.regRenderer(item, i, ModObject.blockEndermanSkull.getUnlocalisedName() + "_" + st.name);
    }
  }

  @Override
  public int damageDropped(@Nonnull IBlockState state) {
    SkullType var = state.getValue(VARIANT);
    return var.ordinal();
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    @Nonnull
    SkullType var = SkullType.getTypeFromMeta(meta);
    return getDefaultState().withProperty(VARIANT, var);
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    SkullType var = state.getValue(VARIANT);
    return var.ordinal();
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { VARIANT });
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public @Nonnull EnumBlockRenderType getRenderType(@Nonnull IBlockState bs) {
    return EnumBlockRenderType.MODEL;
  }

  @Override
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull ItemStack stack) {
    int inc = MathHelper.floor_double(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
    float facingYaw = -22.5f * inc;
    TileEndermanSkull te = getTileEntity(world, pos);
    if (te != null) {
      te.setYaw(facingYaw);
    }
    if (world.isRemote) {
      return;
    }
    world.setBlockState(pos, getStateFromMeta(stack.getItemDamage()));
    world.notifyBlockUpdate(pos, state, state, 3);    
  }

  @Deprecated
  @Override
  public @Nonnull AxisAlignedBB getSelectedBoundingBox(@Nonnull IBlockState bs, @Nonnull World worldIn, @Nonnull BlockPos pos) {
    TileEndermanSkull tileEntity = getTileEntity(worldIn, pos);
    if (tileEntity != null) {
      tileEntity.lookingAt = 20;
    }
    return super.getSelectedBoundingBox(bs, worldIn, pos);
  }

  //TODO: 1.9 Thaumcraft
//  @Override
//  @Optional.Method(modid = "Thaumcraft")
//  public boolean canStabaliseInfusion(World world, BlockPos pos) {
//    return true;
//  }
}
