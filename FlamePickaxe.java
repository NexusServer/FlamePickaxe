package nexus;

public class FlamePickaxe extends PluginBase implements Listener{
	List<Player> enchanting=new List<Player>();
	Config config;
	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this,this);
		this.getDataFolder().mkdirs();
		//this.saveResource("config.yml",false);
		this.config=new Config(this.getDataFolder()+"/config.yml",Config.YAML);
		this.config.set(4,1);
		this.config.set(14,266);
		this.config.set(15,265);
		this.config.set(16,263);
		this.config.set(17,"263:1");
		this.config.set(21,"351:4");
		this.config.set(56,264);
		this.config.set(73,331);
		this.config.set(129,388);
		this.config.set(153,406);
		this.config.set(162,"263:1");
	}
	@Override
	public void onDisable(){
		this.config.save();
	}
	@EventHandler
	public void onTouch(PlayerInteractEvent ev){
		Item item=ev.getItem();
		int item_id=item.getId();
		Player player=ev.getPlayer();
		Inventory inventory=player.getInvenyory();
		if(item_id==Item.EMCHANTED_BOOK&&item.hasEnchantments()){
			ArrayList<Enchantment> enchantments=new ArrayList<Enchantment>(Arrays.asList(item.getEnchantments()));
			if(!enchantments.contains(new EnchantmentFireAspect)){
				return;
			}
			enchanting.put(player);
			player.sendTip("§6화염 효과를 붙일 곡괭이로 다시 터치해주세요.");
			inventory.removeItem(new Item(Item.ENCHANTED_BOOK,0,1));
			ev.setCancelled();
			return;
		}
		if(enchanting.contains(player)){
			if(!item.isPickaxe()){
				enchanting.remove(player);
				player.sendTip("§4취소되었습니다.");
				Item book=new Item(Item.ENCHANTED_BOOK,0,1);
				book.addEnchantment(new Enchantment(Enchantment.ID_FIRE_ASPECT, "fire", 1, EnchantmentType.SWORD));
				inventory.addItem(book);
				ev.setCancelled();
				return;
			}
			item.addEnchantment(new Enchantment(Enchantment.ID_FIRE_ASPECT, "fire", 1, EnchantmentType.SWORD));
			player.sendTip("§6화염 효과 추가 성공 !");
			ev.setCancelled();
			return;
		}
	}
	
	@Override
	public void onBreak(BlockBreakEvent ev){
		Item item=ev.getItem();
		int item_id=item.getId();
		Block block=ev.getBlock();
		int block_id=block.getId();
		Player player=ev.getPlayer();
		Inventory inventory=player.getInvenyory();
		if(item.isPickaxe()&&item.hasEnchantments()){
			ArrayList<Enchantment> enchantments=new ArrayList<Enchantment>(Arrays.asList(item.getEnchantments()));
			if(!enchantments.contains(new EnchantmentFireAspect)){
				return;
			}
			if(canFlame(block_id)){
				String $burned=this.config.getString(block_id);//구워진 아이템
				
				int burned;
				int burned_damage;
				if(!isStringInt($burned)){//문자열일경우
					burned=$burned.split(":")[0];
					burned_damage=$burned.split(":")[1];
				}
				else{
					burned=Integer.parseInt($burned);
					burned_damage=0;
				}
				item.setDamage(item.getDamage()-1);
				Item[] drops=new Item(bruned,burned_damage,1);
				ev.setDrops(drops);
			}
		}
	}
	boolean canFlame(int block_id){
		if(!this.config.exists(block_id)){
			return false;
		}
		return true;
	}
	boolean isStringInt(String s){
		try{
			Integer.parseInt(s);
			return true;
    }catch(NumberFormatException e){
			return false;
    }
  }
}