package net.techcable.sonarpet.nms.versions.v1_13_R2;

import com.google.common.base.Preconditions;
import lombok.*;

import java.lang.invoke.MethodHandle;

import net.minecraft.server.v1_13_R2.*;
import net.techcable.pineapple.SneakyThrow;
import net.techcable.pineapple.reflection.PineappleField;
import net.techcable.pineapple.reflection.Reflection;
import net.techcable.sonarpet.nms.EntityRegistry;

public class NMSEntityRegistry implements EntityRegistry {
    //
    // Very rarely breaks
    // Only broken once on 1.13
    //

    /*
     * NOTE: Although classes are rarely obfuscated,
     * EntityTypes.a should really be EntityTypes.Builder (according to MCP).
     * EntityTypes is also a misleading name,
     * it should really be EntityType since it represents only a single entity.
     */
    @SuppressWarnings("unchecked")
    private static final PineappleField<RegistryMaterials<Class<? extends Entity>>, RegistryID<Class<?>>> REGISTRY_ID_FIELD =
            (PineappleField) PineappleField.findFieldWithType(RegistryMaterials.class, RegistryID.class);
    private static final MethodHandle REGISTER_ENTITY_METHOD =
            Reflection.getMethod(EntityTypes.class, "a", String.class, EntityTypes.a.class); // EntityList.register

    @Override
    @SneakyThrows
    public void registerEntityClass(Class<?> entityClass, String name, int id) {
        /*
         * EntityTypes uses the following code to register an entity
         * ````
         * AREA_EFFECT_CLOUD = a(
         *     "area_effect_cloud",
         *     EntityTypes.a.a(
         *         EntityAreaEffectCloud.class,
         *         EntityAreaEffectCloud::new
         *     )
         * )
         * ```
         * This roughHuhly deobfuscates as
         * ````
         * EntityType.register(
         *     "area_effect_cloud",
         *     EntityTypes.Builder.create(
         *         EntityAreaEffectCloud.class,
         *         EntityAreaEffectClood::new
         *     )
         * )
         * NOTE: EntityTypes.Builder.create is missing from MCP
         * ````
         */
        MethodHandle constructor = Reflection.getConstructor(entityClass, World.class);
        // TODO: This seems to use whatever id it wants
        REGISTER_ENTITY_METHOD.invoke(name, EntityTypes.a.a(
                entityClass.asSubclass(Entity.class),
                (world) -> {
                    try {
                        return (Entity) constructor.invoke(world);
                    } catch (Throwable t) {
                        throw SneakyThrow.sneakyThrow(t);
                    }
                }
        ));
    }

    @Override
    public void unregisterEntityClass(Class<?> entityClass, String name, int id) {
        // TODO: unregister
    }

    @Override
    public int getEntityId(Class<?> entityClass) {
        MinecraftKey key = EntityTypes.clsToKeyMap.get(entityClass.asSubclass(Entity.class));
        Preconditions.checkNotNull(key, "Invalid class %s", entityClass);
        EntityTypes entityType = IRegistry.ENTITY_TYPE.get(key);
        /*
         * I was stuck on how to use ids until I looked in the packet system.
         * In PacketPlayOutSpawnEntityLiving: `IRegistry.ENTITY_TYPE.a(var1.P())`
         * This deobfuscates to `IRegistry.ENTITY_TYPE.getId(var1.getType())`
         */
        // MCP: IRegistry.getId
        return IRegistry.ENTITY_TYPE.a(entityType);
    }

    @Override
    public Class<?> getEntityClass(int id) {
        EntityTypes entityType = IRegistry.ENTITY_TYPE.fromId(id);
        // MCP: EntityType.getEntityClass
        return entityType.c();
    }


    @Override
    public String getEntityName(Class<?> entityClass) {
        MinecraftKey key = EntityTypes.clsToKeyMap.get(entityClass.asSubclass(Entity.class));
        Preconditions.checkNotNull(key, "Invalid class %s", entityClass);
        EntityTypes entityType = IRegistry.ENTITY_TYPE.get(key);
        return EntityTypes.getName(entityType).getKey();
    }

    @SuppressWarnings("unchecked") // I am already doing dark magic
    @Override
    public void registerEntityId(int id, Class<?> entityClass) {
        REGISTRY_ID_FIELD.get((RegistryMaterials) IRegistry.ENTITY_TYPE)
                .a(entityClass, id); // IntIdentityHashBiMap.put
    }

    @SuppressWarnings("unchecked") // I am already doing dark magic
    @Override
    public void unregisterEntityId(int id, Class<?> entityClass) {
        REGISTRY_ID_FIELD.get((RegistryMaterials) IRegistry.ENTITY_TYPE)
                .a(null, id); // IntIdentityHashBiMap.put
    }
}
