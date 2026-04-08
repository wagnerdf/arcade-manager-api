package com.wagnerdf.arcademanager.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wagnerdf.arcademanager.dto.CreatePlatformRequest;
import com.wagnerdf.arcademanager.dto.UpdatePlatformRequest;
import com.wagnerdf.arcademanager.entity.Platform;
import com.wagnerdf.arcademanager.exception.BusinessException;
import com.wagnerdf.arcademanager.repository.PlatformRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlatformService {

    private final PlatformRepository platformRepository;

    /**
     * Cria uma nova plataforma (console) no sistema.
     * 
     * Regras aplicadas:
     * - O nome da plataforma deve ser único
     * - A verificação ignora diferença entre maiúsculas e minúsculas
     */
    public Platform createPlatform(CreatePlatformRequest request) {

        if (platformRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException("Plataforma já cadastrada", HttpStatus.CONFLICT);
        }
        
        String fileName = null;

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            fileName = saveImage(request.getImage());
        }

        Platform platform = Platform.builder()
                .name(request.getName())
                .manufacturer(request.getManufacturer())
                .releaseYear(request.getReleaseYear())
                .unitsSold(request.getUnitsSold())
                .description(request.getDescription())
                .imageName(fileName)
                .build();

        return platformRepository.save(platform);
    }
    
    /**
     * Retorna todas as plataformas cadastradas no sistema.
     * 
     * Utilizado principalmente para:
     * - seleção de plataforma ao cadastrar jogos
     * - exibição da lista de consoles disponíveis
     */
    public List<Platform> getAllPlatforms() {
        return platformRepository.findAll();
    }
    
    /**
     * Atualiza os dados de uma plataforma existente.
     * 
     * Regras aplicadas:
     * - A plataforma deve existir
     * - O novo nome não pode duplicar outra plataforma
     */
    public Platform updatePlatform(String id, UpdatePlatformRequest request) {

        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plataforma não encontrada", HttpStatus.NOT_FOUND));

        if (platformRepository.existsByNameIgnoreCase(request.getName())
        		&& !platform.getName().equalsIgnoreCase(request.getName())) {
            throw new BusinessException("Já existe uma plataforma com esse nome", HttpStatus.CONFLICT);
        }

        platform.setName(request.getName());
        platform.setManufacturer(request.getManufacturer());
        platform.setReleaseYear(request.getReleaseYear());
        platform.setUnitsSold(request.getUnitsSold());
        platform.setDescription(request.getDescription());
        
        // 🔥 TRATAMENTO DA IMAGEM
        if (request.getImage() != null && !request.getImage().isEmpty()) {

            // deletar imagem antiga
            deleteImage(platform.getImageName());

            // salvar nova
            String newImage = saveImage(request.getImage());
            platform.setImageName(newImage);
        }

        return platformRepository.save(platform);
    }
    
    /**
     * Remove uma plataforma do sistema.
     * 
     * Regras aplicadas:
     * - A plataforma deve existir no sistema
     */
    public void deletePlatform(String id) {

        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Plataforma não encontrada", HttpStatus.NOT_FOUND));

        // 🔥 DELETA IMAGEM ANTES
        deleteImage(platform.getImageName());

        // 🔥 DELETA DO BANCO
        platformRepository.delete(platform);
    }
    
    /**
     * Salva uma imagem no diretório local do servidor.
     *
     * Regras:
     * - Cria o diretório caso não exista
     * - Gera um nome único para evitar conflitos
     * - Lança exceção em caso de erro no salvamento
     *
     * @param file arquivo enviado na requisição
     * @return nome do arquivo salvo
     */
    private String saveImage(MultipartFile file) {

    	String uploadDir = System.getProperty("user.dir") + "/uploads/platform/";

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null) {
            throw new BusinessException("Nome do arquivo inválido", HttpStatus.BAD_REQUEST);
        }

        String fileName = UUID.randomUUID() + "_" + originalName;

        try {
            file.transferTo(new File(directory, fileName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("Erro ao salvar imagem: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return fileName;
    }
    
    /**
     * Configuração para disponibilizar imagens de plataformas via HTTP.
     *
     * Mapeamento:
     * - URL: /images/platform/**
     * - Diretório local: uploads/platform/
     *
     * Exemplo:
     * Arquivo salvo em: uploads/platform/abc.png
     * Acesso via: http://localhost:8080/images/platform/abc.png
     *
     * Usado para permitir que o frontend acesse imagens salvas localmente
     * sem necessidade de serviços externos.
     */
    @Configuration
    public class WebConfig implements WebMvcConfigurer {

    	 /**
         * Registra o handler responsável por servir arquivos estáticos
         * do diretório de imagens de plataformas.
         */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/images/platform/**")
                    .addResourceLocations("file:uploads/platform/");
        }
    }
    
    /**
     * Remove uma imagem do diretório local do servidor.
     *
     * Regras:
     * - Não executa ação caso o nome da imagem seja nulo
     * - Verifica se o arquivo existe antes de deletar
     * - Em caso de falha, registra mensagem no console
     *
     * @param imageName nome do arquivo a ser removido
     */
    private void deleteImage(String imageName) {

        if (imageName == null) return;

        String uploadDir = System.getProperty("user.dir") + "/uploads/platform/";

        File file = new File(uploadDir + imageName);

        if (file.exists()) {
            boolean deleted = file.delete();

            if (!deleted) {
                System.out.println("Não conseguiu deletar imagem: " + imageName);
            }
        }
    }
}